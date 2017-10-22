/**
 * Copyright 2017 SPeCS.
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

package pt.up.fe.specs.lara.doc.comments;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.collections.MultiMap;

public class LaraDocComment {

    private final String text;
    // private final List<JsDocTag> tags;
    private final MultiMap<String, JsDocTag> currentTags;
    // private final Set<String> currentTags;
    private final Map<String, JsDocTag> inputs;

    public LaraDocComment() {
        this("", Collections.emptyList());
    }

    public LaraDocComment(String text, List<JsDocTag> tags) {
        this.text = text;
        // this.tags = new ArrayList<>(tags);
        // this.currentTags = new HashMap<>();
        inputs = new HashMap<>();
        this.currentTags = new MultiMap<>();
        tags.stream().forEach(tag -> currentTags.put(tag.getTagName(), tag));
    }

    @Override
    public String toString() {
        // return "LaraDocComment text:'" + text + "'; tags: " + tags;
        return "LaraDocComment text:'" + text + "'; tags: " + currentTags;
    }

    public boolean hasTag(JsDocTagName tagName) {
        return hasTag(tagName.getTagName());
    }

    public boolean hasTag(String tagName) {
        return currentTags.containsKey(tagName);
    }

    // public List<JsDocTag> getTags() {
    // return tags;
    // }

    public JsDocTag getLastTag(JsDocTagName tagName) {
        return getLastTag(tagName.getTagName());
    }

    public JsDocTag getLastTag(String tagName) {
        // return currentTags.get(tagName);
        List<JsDocTag> tags = currentTags.get(tagName);
        if (tags.isEmpty()) {
            return null;
        }

        return SpecsCollections.last(tags);
    }

    public String getText() {
        return text;
    }

    public LaraDocComment addTag(JsDocTag tag) {
        if (!processTag(tag)) {
            return this;
        }

        // tags.add(tag);
        this.currentTags.put(tag.getTagName(), tag);
        return this;
    }

    private boolean processTag(JsDocTag tag) {

        if (tag.getTagName().equals("param")) {

            // If param tag, force name parameter
            if (!tag.hasProperty(JsDocTagProperty.NAME)) {
                SpecsLogs.msgInfo("!Ignoring JsDoc tag '" + tag + "', is a 'param' tag and has no 'name' property");
                return false;
            }

            // Add param to table
            inputs.put(tag.getValue(JsDocTagProperty.NAME), tag);
        }

        return true;
    }

    public LaraDocComment addTagIfMissing(JsDocTag tag) {
        if (currentTags.containsKey(tag.getTagName())) {
            return this;
        }

        return addTag(tag);
    }

    public JsDocTag getInput(String paramName) {
        return inputs.get(paramName);
    }

    public JsDocTag getTag(JsDocTagName tagName) {
        return tryTag(tagName).orElseThrow(() -> new RuntimeException("Tag '" + tagName + "' not found"));
    }

    public Optional<JsDocTag> tryTag(JsDocTagName tagName) {
        List<JsDocTag> tags = currentTags.get(tagName.getTagName());
        if (tags.isEmpty()) {
            return Optional.empty();
        }

        if (tags.size() > 1) {
            SpecsLogs
                    .msgInfo("Asked for a single tag '" + tagName + "' but there are several, returning the first one");
        }

        return Optional.of(tags.get(0));
    }

    public List<JsDocTag> getTags(JsDocTagName tagName) {
        return currentTags.get(tagName.getTagName());
    }

    public List<String> getParameters() {
        return getTags(JsDocTagName.PARAM).stream()
                .map(jsdoctag -> jsdoctag.getValue(JsDocTagProperty.NAME))
                .collect(Collectors.toList());
    }
}
