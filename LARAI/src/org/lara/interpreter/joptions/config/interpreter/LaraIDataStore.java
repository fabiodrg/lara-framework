/*
 * Copyright 2013 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */
package org.lara.interpreter.joptions.config.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.lara.interpreter.api.WeaverApis;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.utils.Tools;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

import larai.LaraI;
import larai.larabundle.LaraBundle;
import larai.lararesource.LaraResource;
import pt.up.fe.specs.git.GitRepos;
import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.lara.LaraApis;
import pt.up.fe.specs.lara.aspectir.Argument;
import pt.up.fe.specs.lara.commonlang.LaraCommonLang;
import pt.up.fe.specs.tools.lara.logging.LaraLog;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.properties.SpecsProperties;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.StringList;

/**
 * TODO: Should deprecate and just use DataStore directly?
 * 
 * @author JoaoBispo
 *
 */
public class LaraIDataStore implements LaraiKeys {

    private static final String GIT_QUERY_FOLDER = "folder";
    private static final Set<String> GIT_URL_QUERIES = new HashSet<>(Arrays.asList(GIT_QUERY_FOLDER));

    public static final String CONFIG_FILE_NAME = "larai.properties";
    private final DataStore dataStore;
    private final LaraI larai;
    private Tools tools = null;
    // private final DataStore weaverDataStore;
    private final List<ResourceProvider> laraAPIs;

    private final GitRepos gitRepos;

    public LaraIDataStore(LaraI lara, DataStore dataStore, WeaverEngine weaverEngine) {
        larai = lara;

        this.dataStore = dataStore;
        this.gitRepos = new GitRepos();

        // weaverDataStore = DataStore.newInstance("Weaver Arguments");
        laraAPIs = new ArrayList<>();
        laraAPIs.addAll(LaraApis.getApis());
        laraAPIs.addAll(WeaverApis.getApis());
        laraAPIs.addAll(LaraCommonLang.getLaraCommonLangApi());
        laraAPIs.addAll(weaverEngine.getAspectsAPI());

        // laraAPIs = weaverEngine.getAspectsAPI();
        for (WeaverOption option : weaverEngine.getOptions()) {
            DataKey<?> key = option.dataKey();
            Optional<?> value = dataStore.getTry(key);
            if (value.isPresent()) {
                // weaverDataStore.setRaw(key, value.get());
                dataStore.setRaw(key, value.get());
            }
        }
        setLaraProperties();
        // System.out.println("\n\n" + this.dataStore);
        // System.out.println(".........................");
        // System.out.println("\n\n" + dataStore);

    }

    /**
     * Set an option on lara according to the value given, if the option exists on the enum {@link Argument}
     *
     * @param option
     * @param value
     * @return
     */
    private void setLaraProperties() {
        if (!dataStore.hasValue(LaraiKeys.LARA_FILE)) {

            throw new LaraIException(
                    "The lara aspect file is mandatory! Please define the input lara file (e.g.: aspect.lara)");
        }
        if (dataStore.hasValue(LaraiKeys.LOG_FILE)) {
            OptionalFile logFile = dataStore.get(LaraiKeys.LOG_FILE);
            if (logFile.isUsed()) {
                larai.out.addFileStream(logFile.getFile());
            }
        }

        if (dataStore.hasValue(LaraiKeys.VERBOSE)) {
            int level = dataStore.get(LaraiKeys.VERBOSE).ordinal();
            larai.out.setLevel(level);
        }

        if (dataStore.hasValue(LaraiKeys.OUTPUT_FOLDER)) {
            File output = dataStore.get(LaraiKeys.OUTPUT_FOLDER);
            SpecsIo.mkdir(output);
        }

    }

    public Tools getTools() {
        if (tools == null) {
            setTools(createTools(dataStore, larai));
        }
        return tools;
    }

    private static Tools createTools(DataStore dataStore, LaraI larai) {
        try {

            if (dataStore.hasValue(LaraiKeys.TOOLS_FILE)) {

                OptionalFile optionalFile = dataStore.get(LaraiKeys.TOOLS_FILE);

                if (optionalFile.isUsed()) {
                    return new Tools(optionalFile.getFile());
                }

            }

        } catch (final FileNotFoundException e) {
            larai.out.warn("Could not find tools.xml in the given path");
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            larai.out.warn("Could not parse tools.xml: " + e.getLocalizedMessage());
        }
        return null;
    }

    public void setTools(Tools tools) {
        this.tools = tools;
    }

    /**
     * Right now the arguments are going as a single String!
     *
     * @return
     */
    public DataStore getWeaverArgs() {

        // return weaverDataStore;
        return dataStore;
    }

    public File getLaraFile() {
        return dataStore.get(LaraiKeys.LARA_FILE);
    }

    public String getMainAspect() {
        if (dataStore.hasValue(LaraiKeys.MAIN_ASPECT)) {
            return dataStore.get(LaraiKeys.MAIN_ASPECT);
        }
        return "";
    }

    public FileList getWorkingDir() {
        return getTrySources(LaraiKeys.WORKSPACE_FOLDER);
    }

    public List<File> getExtraSources() {
        if (dataStore.hasValue(LaraiKeys.WORKSPACE_EXTRA)) {
            // test1.c$prefix$test1.c;test2.c
            return new ArrayList<>(dataStore.get(WORKSPACE_EXTRA).keySet());
            /*
            String workspaceExtra = dataStore.get(WORKSPACE_EXTRA);
            
            List<String> paths = PathList.parse(workspaceExtra);
            
            // Split using ;
            // String[] paths = workspaceExtra.split(";");
            List<File> extraSources = new ArrayList<>();
            
            for (String path : paths) {
                // Trim
                String trimmedPath = path.strip();
            
                // Ignore if empty
                if (trimmedPath.isEmpty()) {
                    continue;
                }
            
                File parsedFile = KeyFactory.customGetterFile(new File(trimmedPath), dataStore, false, false, false,
                        true);
            
                extraSources.add(parsedFile);
            }
            
            return extraSources;
            */
        }

        return Collections.emptyList();
    }

    public boolean isDebug() {
        if (dataStore.hasValue(LaraiKeys.DEBUG_MODE)) {
            return dataStore.get(LaraiKeys.DEBUG_MODE);
        }
        return false;
    }

    public boolean isRestricMode() {
        return dataStore.get(LaraiKeys.RESTRICT_MODE);
    }

    public boolean useStackTrace() {
        if (dataStore.hasValue(LaraiKeys.TRACE_MODE)) {
            return dataStore.get(LaraiKeys.TRACE_MODE);
        }
        return false;
    }

    public File getOutputDir() {
        return getTryFolder(LaraiKeys.OUTPUT_FOLDER);
    }

    private File getTryFolder(DataKey<File> folder) {
        if (dataStore.hasValue(folder)) {
            return dataStore.get(folder);
        }
        return new File(".");
    }

    private FileList getTrySources(DataKey<FileList> folder) {

        return dataStore.get(folder);
        /*
        if (dataStore.hasValue(folder)) {
            return dataStore.get(folder);
        }
        return FileList.newInstance(new File("."));
        */
    }

    public FileList getIncludeDirs() {
        if (dataStore.hasValue(LaraiKeys.INCLUDES_FOLDER)) {
            FileList includesFolder = dataStore.get(LaraiKeys.INCLUDES_FOLDER);

            return includesFolder;
        }
        return FileList.newInstance();
    }

    public StringList getExternalDependencies() {
        if (dataStore.hasValue(LaraiKeys.EXTERNAL_DEPENDENCIES)) {
            return dataStore.get(LaraiKeys.EXTERNAL_DEPENDENCIES);
        }

        return StringList.newInstance();
    }

    public OptionalFile getReportFile() {
        if (dataStore.hasValue(LaraiKeys.REPORT_FILE)) {
            return dataStore.get(LaraiKeys.REPORT_FILE);
        }
        return OptionalFile.newInstance(null);
    }

    public OptionalFile getMetricsFile() {
        if (dataStore.hasValue(LaraiKeys.METRICS_FILE)) {
            return dataStore.get(LaraiKeys.METRICS_FILE);
        }
        return OptionalFile.newInstance(null);
    }

    public boolean isLaraLoc() {
        if (dataStore.hasValue(LaraiKeys.LARA_LOC)) {
            return dataStore.get(LaraiKeys.LARA_LOC);
        }
        return false;
    }

    public String getAspectArgumentsStr() {
        if (dataStore.hasValue(LaraiKeys.ASPECT_ARGS)) {

            String aspectArgs = dataStore.get(LaraiKeys.ASPECT_ARGS);

            // Parse aspect args (e.g., in case it is a file)
            return parseAspectArgs(aspectArgs);

        }
        return "";
    }

    private static String parseAspectArgs(String aspectArgs) {
        // If string ends with '.properties', interpret as a path to a properties file
        if (aspectArgs.trim().endsWith(".properties")) {
            File properties = SpecsIo.existingFile(aspectArgs);
            return SpecsProperties.newInstance(properties).toJson();
        }

        return aspectArgs;
    }

    public boolean isJavaScriptStream() {
        if (dataStore.hasValue(LaraiKeys.LOG_JS_OUTPUT)) {
            return dataStore.get(LaraiKeys.LOG_JS_OUTPUT);
        }
        return false;
    }

    public List<ResourceProvider> getLaraAPIs() {
        return laraAPIs;
    }

    public Map<String, String> getBundleTags() {
        // System.out.println("DATA STORE:" + dataStore);
        if (dataStore.hasValue(LaraiKeys.BUNDLE_TAGS)) {
            // System.out.println("BUNDLE TAGS:" + dataStore.get(LaraiKeys.BUNDLE_TAGS));
            return parseBundleTags(dataStore.get(LaraiKeys.BUNDLE_TAGS));
        }
        return Collections.emptyMap();
    }

    public JsEngineType getJsEngine() {
        if (dataStore.hasValue(LaraiKeys.JS_ENGINE)) {
            return dataStore.get(LaraiKeys.JS_ENGINE);
        }
        return JS_ENGINE.getDefault().get();
    }

    private Map<String, String> parseBundleTags(String bundleTagsString) {
        Map<String, String> bundleTags = new HashMap<>();

        if (bundleTagsString.trim().isEmpty()) {
            return bundleTags;
        }

        // Split around the comma
        String[] tagPairs = bundleTagsString.split(",");
        for (String tagPair : tagPairs) {
            int equalIndex = tagPair.indexOf('=');
            Preconditions.checkArgument(equalIndex != -1,
                    "Malformed 'Bundle Tags' option, found a tag-value pair without equal sign (=): '"
                            + bundleTagsString + "'");

            String tag = tagPair.substring(0, equalIndex);
            String value = tagPair.substring(equalIndex + 1, tagPair.length());

            bundleTags.put(tag, value);
        }

        return bundleTags;
    }

    public FileList getProcessedIncludeDirs(WeaverEngine weaverEngine) {
        FileList includeDirs = getIncludeDirs();

        // SpecsLogs.debug(() -> "Original LARA include dirs: " + includeDirs);

        // Process GIT repositories
        FileList includeDirsAfterGit = processExternalDependencies(includeDirs);
        // SpecsLogs.debug(() -> "Include dirs after GIT repositories: " + includeDirsAfterGit);

        // Process Bundles
        LaraBundle laraBundle = new LaraBundle(weaverEngine.getName(), getBundleTags());
        FileList includeDirsAfterBundles = laraBundle.process(includeDirsAfterGit);
        // SpecsLogs.debug(() -> "Include dirs after bundles: " + includeDirsAfterBundles);

        // Process LARA Resources, but only of active bundles
        LaraResource laraResource = new LaraResource(weaverEngine);
        FileList includeDirsAfterResources = laraResource.process(includeDirsAfterBundles);
        // SpecsLogs.debug(() -> "Include dirs after LARA resources: " + includeDirsAfterResources);

        return includeDirsAfterResources;
    }

    private FileList processExternalDependencies(FileList includeDirs) {
        // Check if there are external dependencies
        List<String> externalDependencies = getExternalDependencies().getStringList();

        if (externalDependencies.isEmpty()) {
            return includeDirs;
        }

        List<File> files = includeDirs.getFiles();

        // Process each dependency
        for (String externalDependency : externalDependencies) {
            processExternalDependency(externalDependency, files);
        }

        return FileList.newInstance(files);
        // List<File> files = new ArrayList<>();
        //
        // for (File file : includeDirs.getFiles()) {
        // files.add(file);
        // }
        //
        // return includeDirs.newInstance(files);
    }

    private void processExternalDependency(String externalDependency, List<File> files) {
        var urlTry = SpecsIo.parseUrl(externalDependency);
        if (urlTry.isEmpty()) {
            LaraLog.info("Could not parse URL of external dependency: " + externalDependency);
            return;
        }

        var url = urlTry.get();

        // String lowercaseDep = externalDependency.toLowerCase();
        // String lowercaseDep = url.getPath().toLowerCase();
        String protocol = url.getProtocol().toLowerCase();
        String path = url.getPath().toLowerCase();

        if (path.endsWith(".git") || protocol.equals("git")) {
            Map<String, String> query = SpecsIo.parseUrlQuery(url);
            var gitDependency = url.getQuery() != null ? externalDependency.replace("?" + url.getQuery(), "")
                    : externalDependency;
            processGitDependency(gitDependency, files, query);
            return;
        }

        if (protocol.equals("http") || protocol.equals("https")) {
            LaraLog.info("Pure HTTP links not yet supported as external dependency: '" + externalDependency + "'");
            return;
        }

        LaraLog.info("Could not determine the external dependency link kind: '" + externalDependency + "'");
    }

    private void processGitDependency(String externalDependency, List<File> files, Map<String, String> query) {
        // Check queries
        if (!query.isEmpty()) {
            List<String> unsupportedQueries = query.keySet().stream()
                    .map(String::toLowerCase)
                    .filter(key -> !GIT_URL_QUERIES.contains(key))
                    .collect(Collectors.toList());

            if (!unsupportedQueries.isEmpty()) {
                LaraLog.info("Found unsuppoted queries " + unsupportedQueries + " in git URL. Supported queries: "
                        + GIT_URL_QUERIES);
            }
        }

        File gitRepoFolder = gitRepos.getFolder(externalDependency);
        // File gitRepoFolder = SpecsGit.parseRepositoryUrl(externalDependency);
        if (gitRepoFolder == null || !gitRepoFolder.isDirectory()) {
            LaraLog.info("Could not prepare external dependency '" + externalDependency + "'");
            return;
        }

        // Check if there is a specific folder specified
        var foldername = query.get(GIT_QUERY_FOLDER);
        if (foldername != null) {
            var specificFolder = new File(gitRepoFolder, foldername);
            if (!specificFolder.isDirectory()) {
                LaraLog.info("Repo folder '" + specificFolder + "' does not exist");
            } else {
                LaraLog.info("Adding folder '" + specificFolder + "' as LARA include");
                gitRepoFolder = specificFolder;
            }
        }

        // Add repository
        files.add(gitRepoFolder);
    }

}
