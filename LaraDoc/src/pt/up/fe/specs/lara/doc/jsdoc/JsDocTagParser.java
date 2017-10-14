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

package pt.up.fe.specs.lara.doc.jsdoc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.stringparser.StringParser;
import pt.up.fe.specs.util.stringparser.StringParsers;

public class JsDocTagParser {

    private static final Map<String, JsDocTagLineParser> DEFAULT_PARSERS;
    static {
        DEFAULT_PARSERS = new HashMap<>();
        DEFAULT_PARSERS.put("class", JsDocTagParser::parseTagOnly);
        DEFAULT_PARSERS.put("constructor", (tagName, contents) -> JsDocTagParser.parseTagOnly("class", contents));
    }

    private final Map<String, JsDocTagLineParser> tagParsers;

    private final Set<String> seenUnsupportedTags;

    public JsDocTagParser() {
        this(DEFAULT_PARSERS);
    }

    public JsDocTagParser(Map<String, JsDocTagLineParser> tagParsers) {
        this.tagParsers = tagParsers;

        seenUnsupportedTags = new HashSet<>();
    }

    public JsDocTag parse(String tagComment) {
        System.out.println("TAG COMMENT:" + tagComment);
        StringParser currentTagComment = new StringParser(tagComment);

        JsDocTag tag = parsePrivate(currentTagComment);
        Preconditions.checkArgument(currentTagComment.isEmpty(),
                "Expected tag contents to be completely consumed, remained '" + currentTagComment + "'");
        return tag;
    }

    private JsDocTag parsePrivate(StringParser currentTagComment) {

        // Parse tag
        currentTagComment.apply(StringParsers::parseString, "@");
        String tagName = currentTagComment.apply(StringParsers::parseWord);

        JsDocTagLineParser tagParser = tagParsers.get(tagName);
        if (tagParser != null) {
            JsDocTag tag = tagParser.parse(tagName, currentTagComment);
            // Add remaining data as content
            if (!currentTagComment.isEmpty()) {
                tag.addContent(currentTagComment.clear());
            }

            return tag;
        }

        // Check if tag was already seen
        if (!seenUnsupportedTags.contains(tagName)) {
            seenUnsupportedTags.add(tagName);
            SpecsLogs.msgInfo("JsDoc tag not supported yet: " + tagName);
        }

        JsDocTag tag = new JsDocTag(tagName);

        tag.addContent(currentTagComment.clear());

        return tag;
    }

    public static JsDocTag parseTagOnly(String tagName, StringParser contents) {
        // No more parsing to do
        return new JsDocTag(tagName);
    }

}
