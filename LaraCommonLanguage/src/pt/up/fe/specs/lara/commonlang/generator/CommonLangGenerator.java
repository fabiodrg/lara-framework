/**
 * Copyright 2020 SPeCS.
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

package pt.up.fe.specs.lara.commonlang.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;

import pt.up.fe.specs.lara.commonlang.LaraCommonLang;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.Replacer;

/**
 * Generates the LARA files that implement the language specification hierarchy.
 * 
 * @author JBispo
 *
 */
public class CommonLangGenerator {

	private final File outputFolder;

	public static List<ResourceProvider> getGeneratedResources() {
		List<ResourceProvider> resources = new ArrayList<>();

		var langSpec = LaraCommonLang.getLanguageSpecification();

		for (var jp : langSpec.getJpModel().getJoinPointList().getJoinpoint()) {
			resources.add(() -> getJpResource(jp.getClazz()));
		}

		return resources;
	}

	public CommonLangGenerator(File outputFolder) {
		this.outputFolder = outputFolder;
	}

	public void generate() {
		// Read lang spec
		var langSpec = LaraCommonLang.getLanguageSpecification();

		generateJpBase(langSpec.getArtifacts().getGlobalAttributes());

		for (var jp : langSpec.getJpModel().getJoinPointList().getJoinpoint()) {

			System.out.println("JP: " + "weaver/jp/" + jp.getClazz() + ".lara");

			var atts = langSpec.getArtifacts().getAttributes(jp.getClazz());
			atts = (atts == null) ? new ArrayList<>() : atts;

			generate(jp, atts);
		}

		generateIndex();

	}

	private void generateIndex() {
		var jpIndexStr = "";

		final String importTemplate = "import weaver.jp.<SUPER_JP>;\n";

		jpIndexStr += importTemplate.replace("<SUPER_JP>", "JoinPoint");

		var langSpec = LaraCommonLang.getLanguageSpecification();

		for (var jp : langSpec.getJpModel().getJoinPointList().getJoinpoint()) {
			jpIndexStr += importTemplate.replace("<SUPER_JP>", getJoinPointClassName(jp.getClazz()));
		}

		var laraResource = "weaver/jp/JoinPointIndex.lara";
		var laraFile = new File(outputFolder, laraResource);
		SpecsIo.write(laraFile, jpIndexStr);

	}

	private void generateJpBase(Map<String, Attribute> globAtts) {

		var jpBase = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/JpBase.txt");
		var jpName = "JoinPoint";
		var jpBaseStr = jpBase.toString();

		for (var att : globAtts.keySet()) {
			// if att is already defined do not create template
			var attFunc = String.format("JoinPoint.prototype.%s = function(", att);
			var attParamsFunc = String.format("Object.defineProperty(JoinPoint.prototype, \'%s\', {", att);

			if (jpBaseStr.contains(attFunc) || jpBaseStr.contains(attParamsFunc))
				continue;

			var attTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/AttTemplate.txt");
			if (!globAtts.get(att).getParameter().isEmpty()) {
				attTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/AttParamTemplate.txt");
				var params = globAtts.get(att).getParameter().stream().map(param -> param.getName())
						.reduce((param1, param2) -> param1 + "," + param2).get();
				attTemplate.replace("<PARAMS>", params);
			}
			attTemplate.replace("<THIS_JP>", jpName);
			attTemplate.replace("<ATT>", att);
			jpBaseStr += attTemplate.toString();
		}

		var laraResource = "weaver/jp/" + jpName + ".lara";
		var laraFile = new File(outputFolder, laraResource);
		SpecsIo.write(laraFile, jpBaseStr);

	}

	public static String getJoinPointClassName(String jpName) {
		// Upper case first letter
		return StringUtils.capitalize(jpName) + "Jp";
	}

	private void generate(JoinPointType jpType, List<Attribute> atts) {

		var jpTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/JpTemplate.txt");

		var jpName = jpType.getClazz();
		var jpClassName = getJoinPointClassName(jpName);
		var superName = ((JoinPointType) jpType.getExtends()).getClazz();
		var superClassName = superName.equals(jpName) ? "JoinPoint" : getJoinPointClassName(superName);

		jpTemplate.replace("<THIS_JP>", jpClassName);
		jpTemplate.replace("<SUPER_JP>", superClassName);
		jpTemplate.replace("<JP_TYPE>", jpName);
		// System.out.println("TEMPLATE:\n" + jpTemplate);

		var jpTemplateStr = jpTemplate.toString();

		for (var att : atts) {
			if (isAttDefinedInSuper(jpType, att))
				continue;

			var attTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/AttTemplate.txt");
			if (!att.getParameter().isEmpty()) {
				attTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/AttParamTemplate.txt");
				var params = att.getParameter().stream().map(param -> param.getName())
						.reduce((param1, param2) -> param1 + "," + param2).get();
				attTemplate.replace("<PARAMS>", params);
			}
			attTemplate.replace("<THIS_JP>", jpClassName);
			attTemplate.replace("<ATT>", att.getName());
			jpTemplateStr += attTemplate.toString();
		}

		var laraResource = getJpResource(jpName);
		var laraFile = new File(outputFolder, laraResource);
		SpecsIo.write(laraFile, jpTemplateStr);
	}

	public static String getJpResource(String jpName) {
		var jpClassName = getJoinPointClassName(jpName);
		return "weaver/jp/" + jpClassName + ".lara";
	}

	public static Boolean isAttDefinedInSuper(JoinPointType jpType, Attribute att) {
		var jpName = jpType.getClazz();
		var superName = ((JoinPointType) jpType.getExtends()).getClazz();
		if (superName.equals(jpName))
			return false;

		var langSpec = LaraCommonLang.getLanguageSpecification();

		var atts = langSpec.getArtifacts().getAttributes(superName);
		atts = (atts == null) ? new ArrayList<>() : atts;

		if (atts.stream().anyMatch(attElem -> attElem.getName().equals(att.getName()))) {
			return true;
		}

		return isAttDefinedInSuper((JoinPointType) jpType.getExtends(), att);

	}
}
