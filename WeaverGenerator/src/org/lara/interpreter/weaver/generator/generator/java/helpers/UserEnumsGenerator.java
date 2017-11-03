/**
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.java.helpers;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.interf.NamedEnum;
import org.lara.language.specification.artifactsmodel.schema.EnumDef;
import org.lara.language.specification.artifactsmodel.schema.EnumValue;
import org.specs.generators.java.classtypes.JavaEnum;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.members.Constructor;
import org.specs.generators.java.members.EnumItem;
import org.specs.generators.java.members.Field;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaTypeFactory;

public class UserEnumsGenerator extends GeneratorHelper {
    private final EnumDef enumDef;

    /**
     * Generate an entity based on the NewObject instance
     * 
     * @param newObject
     *            the new Object to generate
     * @return
     */
    protected UserEnumsGenerator(JavaAbstractsGenerator javaGenerator, EnumDef enumDef) {
        super(javaGenerator);
        this.enumDef = enumDef;
    }

    /**
     * Generate an entity based on the NewObject instance
     * 
     * @param newObject
     *            the new Object to generate
     * @return
     */
    public static JavaEnum generate(JavaAbstractsGenerator javaGenerator, EnumDef enumDef) {
        final UserEnumsGenerator gen = new UserEnumsGenerator(javaGenerator, enumDef);
        return gen.generate();
    }

    /**
     * Generate an entity based on the NewObject instance
     * 
     * @return
     */
    @Override
    public JavaEnum generate() {
        final JavaEnum userEnum = new JavaEnum(enumDef.getName(), javaGenerator.getEnumsPackage());
        Constructor constructor = new Constructor(userEnum);
        constructor.addArgument(JavaTypeFactory.getStringType(), "name");
        constructor.appendCode("this.name = name;");
        Method getName = new Method(JavaTypeFactory.getStringType(), "getName");
        getName.appendCode("return this.name;");
        Method toString = new Method(JavaTypeFactory.getStringType(), "toString");
        toString.appendCode("return getName();");

        userEnum.addInterface(JavaTypeFactory.convert(NamedEnum.class));
        userEnum.add(new Field(JavaTypeFactory.getStringType(), "name"));
        userEnum.add(constructor);
        userEnum.add(getName);
        userEnum.add(toString);
        userEnum.appendComment("\n");
        userEnum.add(JDocTag.AUTHOR, "Lara C.");
        for (final EnumValue enumValue : enumDef.getValue()) {

            final String fieldName = enumValue.getName();
            final String classType = enumValue.getString();
            EnumItem item = new EnumItem(fieldName);
            item.addParameter('"' + classType + '"');
            userEnum.add(item);
        }
        // generateToString(userEnum);
        return userEnum;
    }

    // /**
    // * Generate the toString method based on a json format
    // *
    // * @param uDClass
    // */
    // private static void generateToString(JavaClass uDClass) {
    // final Method toString = new Method(JavaTypeFactory.getStringType(), "toString");
    // toString.add(Annotation.OVERRIDE);
    // // default method
    // // toString.appendCode("return super.toString();");
    // final StringBuffer buff = new StringBuffer("String json = \"{\\n\";\n");
    // for (final Field f : uDClass.getFields()) {
    // final String name = f.getName();
    // buff.append("json += \" ");
    // buff.append(name);
    // buff.append(": \"+get" + StringUtils.firstCharToUpper(name));
    // buff.append("() + \",\\n\";\n");
    // }
    // buff.append("json+=\"}\";\n");
    // buff.append("return json;");
    // toString.setMethodBody(buff);
    // uDClass.add(toString);
    // }
}
