/* Generated By:JJTree: Do not edit this line. ASTFilter.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ASTFilter extends SimpleNode {
    private String prop;
    private String op;
    private String attrType;

    public ASTFilter(int id) {
        super(id);
    }

    public ASTFilter(LARAEcmaScript p, int id) {
        super(p, id);
    }

    public void setProp(String image) {
        prop = image;
    }

    public void setOp(String image) {
        op = image;
    }

    public String getProp() {
        return prop;
    }

    public String getOp() {
        return op;
    }

    @Override
    public String toString() {
        return LARAEcmaScriptTreeConstants.jjtNodeName[id] + "[" + prop + ", " + op + "," + attrType + "]";
    }

    @Override
    public String organize(String type, LanguageSpecificationV2 langSpec) {
        if (type == null) {
            SimpleNode pcParent = (SimpleNode) parent;
            while (!(pcParent instanceof ASTPointcut)) {
                pcParent = (SimpleNode) pcParent.parent;
            }
            getLara().warnln("Cannot validate attribute '" + prop + "' as the joinpoint type '" + pcParent.jjtGetValue()
                    + "' could not be verified. Will use as is");
        } else {
            // final Attribute attribute = artifacts.getAttribute(type, prop);
            var attribute = langSpec.getJoinPoint(type).getAttribute(prop);

            if (attribute.isEmpty()) {
                SimpleNode pcParent = (SimpleNode) parent;
                while (!(pcParent instanceof ASTPointcut)) {
                    pcParent = (SimpleNode) pcParent.parent;
                }
                throw newException(
                        "The attribute '" + prop + "' does not exist on joinpoint '" + pcParent.jjtGetValue() + "'");
            }
            // Use the first occurrence
            setAttrType(attribute.get(0).getReturnType());
        }
        final ASTOperator opNode = (ASTOperator) children[0];
        opNode.organize(new ASTBinaryExpressionSequence(-1));
        op = opNode.getTag();
        ((SimpleNode) children[1]).organize(this);
        return prop;
    }

    /**
     * @return the attrType
     */
    public String getAttrType() {
        return attrType;
    }

    /**
     * @param attrType
     *            the attrType to set
     */
    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    @Override
    public Element getFilterElement(Document doc) {

        final Element matchEl = doc.createElement("op");
        matchEl.setAttribute("name", op);

        final Element idEl = doc.createElement("id");
        idEl.setAttribute("name", prop);
        matchEl.appendChild(idEl);
        ((SimpleNode) getChildren()[1]).toXML(doc, matchEl);

        return matchEl;
    }
}
/*
 * JavaCC - OriginalChecksum=12edf7f7eff79f8e3b5adafce0c6c166 (do not edit this
 * line)
 */
