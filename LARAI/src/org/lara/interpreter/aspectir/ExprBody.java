// xmljavabind
// Thu Feb 23 16:08:26 2017
// Warning:  This file has been automatically generated.
//    Any modifications to the file could be lost.

package org.lara.interpreter.aspectir;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.PrintStream;

/****************************** Class ExprBody ******************************/
public class ExprBody extends Expression implements IElement {
	public String xml_location;
	public String xmltag;
	public Code code;

	public ExprBody(){
		code = null;

	}

	public ExprBody(Element e, 
			String rootName, Document doc) throws DOMException, Exception{
	if (e == null) return;
	xmltag = e.getTagName();
	if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
		throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
	}
	int found_code = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node ExprBody: "+e.getAttributes().item(0).getNodeName());
	}
		Element q0 = doc.createElement("code");
		java.util.List<Element> newChildren = new java.util.ArrayList<>();
		for(int i = 0; i < e.getChildNodes().getLength(); i++) {
			if(!(e.getChildNodes().item(i) instanceof Element)) continue;
			newChildren.add((Element) e.getChildNodes().item(i));
		}
		newChildren.forEach(q0::appendChild);
		e = doc.createElement(e.getNodeName());
		e.appendChild(q0);
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("body")){
			ExprBody _m;
			_m = new ExprBody(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("call")){
			ExprCall _m;
			_m = new ExprCall(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("code")){
			code = new Code(q,"",doc);
			found_code++;
		}
		else if (q.getNodeName().equals("id")){
			ExprId _m;
			_m = new ExprId(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("key")){
			ExprKey _m;
			_m = new ExprKey(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("literal")){
			ExprLiteral _m;
			_m = new ExprLiteral(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("op")){
			ExprOp _m;
			_m = new ExprOp(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("property")){
			Expression _m;
			_m = new Expression(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else
			throw new Exception(" Error unexpected : " + q.getNodeName());
		do{
			n = n.getNextSibling();
		}while(n!= null && !(n instanceof Element));
		q = (n!=null)?(Element)n:null;
	}
	if (!e.getTextContent().trim().isEmpty())
		throw new Exception("Error unexpected: text");
	if (found_code > 1)
		throw new Exception(" too many code: " +found_code+"(max: "+1+ "x)");
	if (found_code < 1)
		throw new Exception(" too few code: " +found_code+"(min: "+1+ "x)");
}

	public ExprBody(String fileName, String rootName) throws Exception {
this(readDocument(fileName), rootName);
	}

	public static Document readDocument(String fileName) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(new File(fileName));
		return doc;
	}

	public ExprBody(Document doc,
			String rootName) throws Exception {
		code = null;
			Element e = (Element)doc.getFirstChild();
			if (e == null) return;
			xmltag = e.getTagName();
			if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
				throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
			}
	int found_code = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node ExprBody: "+e.getAttributes().item(0).getNodeName());
	}
		Element q0 = doc.createElement("code");
		java.util.List<Element> newChildren = new java.util.ArrayList<>();
		for(int i = 0; i < e.getChildNodes().getLength(); i++) {
			if(!(e.getChildNodes().item(i) instanceof Element)) continue;
			newChildren.add((Element) e.getChildNodes().item(i));
		}
		newChildren.forEach(q0::appendChild);
		e = doc.createElement(e.getNodeName());
		e.appendChild(q0);
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("body")){
			ExprBody _m;
			_m = new ExprBody(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("call")){
			ExprCall _m;
			_m = new ExprCall(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("code")){
			code = new Code(q,"",doc);
			found_code++;
		}
		else if (q.getNodeName().equals("id")){
			ExprId _m;
			_m = new ExprId(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("key")){
			ExprKey _m;
			_m = new ExprKey(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("literal")){
			ExprLiteral _m;
			_m = new ExprLiteral(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("op")){
			ExprOp _m;
			_m = new ExprOp(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("property")){
			Expression _m;
			_m = new Expression(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else
			throw new Exception(" Error unexpected : " + q.getNodeName());
		do{
			n = n.getNextSibling();
		}while(n!= null && !(n instanceof Element));
		q = (n!=null)?(Element)n:null;
	}
	if (!e.getTextContent().trim().isEmpty())
		throw new Exception("Error unexpected: text");
	if (found_code > 1)
		throw new Exception(" too many code: " +found_code+"(max: "+1+ "x)");
	if (found_code < 1)
		throw new Exception(" too few code: " +found_code+"(min: "+1+ "x)");
	}
	public void loadXml(Element e, String rootName, Document doc) throws Exception{
	if (e == null) return;
	if (!e.getNodeName().equals(rootName)){
		throw new Exception(" Error unexpected: " + e.getNodeName() +" != "+ rootName);
	}
	int found_code = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node ExprBody: "+e.getAttributes().item(0).getNodeName());
	}
		Element q0 = doc.createElement("code");
		java.util.List<Element> newChildren = new java.util.ArrayList<>();
		for(int i = 0; i < e.getChildNodes().getLength(); i++) {
			if(!(e.getChildNodes().item(i) instanceof Element)) continue;
			newChildren.add((Element) e.getChildNodes().item(i));
		}
		newChildren.forEach(q0::appendChild);
		e = doc.createElement(e.getNodeName());
		e.appendChild(q0);
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("body")){
			ExprBody _m;
			_m = new ExprBody(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("call")){
			ExprCall _m;
			_m = new ExprCall(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("code")){
			code = new Code(q,"",doc);
			found_code++;
		}
		else if (q.getNodeName().equals("id")){
			ExprId _m;
			_m = new ExprId(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("key")){
			ExprKey _m;
			_m = new ExprKey(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("literal")){
			ExprLiteral _m;
			_m = new ExprLiteral(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("op")){
			ExprOp _m;
			_m = new ExprOp(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else if (q.getNodeName().equals("property")){
			Expression _m;
			_m = new Expression(q,"",doc);
			if (exprs.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			exprs.add(_m);
		}
		else
			throw new Exception(" Error unexpected : " + q.getNodeName());
		do{
			n = n.getNextSibling();
		}while(n!= null && !(n instanceof Element));
		q = (n!=null)?(Element)n:null;
	}
	if (!e.getTextContent().trim().isEmpty())
		throw new Exception("Error unexpected: text");
	if (found_code > 1)
		throw new Exception(" too many code: " +found_code+"(max: "+1+ "x)");
	if (found_code < 1)
		throw new Exception(" too few code: " +found_code+"(min: "+1+ "x)");
}

	public IElement getParent(){
	return null;
}

public 	Document getXmlDocument(){
	Document doc = null;	int level = 0;	try {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = dbfac.newDocumentBuilder();
		doc = docBuilder.newDocument();
	} catch (DOMException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	}
	Element tagEl = doc.createElement(xmltag);
		doc.appendChild(tagEl);
	tagEl.setAttribute("desc", ""+desc);
	if (code != null)
		code.writeXml(doc, tagEl,"", level+1);
	for(Expression i_exprs: exprs)
		i_exprs.writeXml(doc,tagEl,"",level+1);
	return doc;}

	public 	void writeXml(Document doc, Element parent, String  rootName, int level){
	String tagName = ((rootName.isEmpty())?xmltag:rootName);
	Element tagEl = doc.createElement(tagName);
	parent.appendChild(tagEl);
		tagEl.setAttribute("desc", ""+desc);
	if (code != null)
		code.writeXml(doc, tagEl,"", level+1);
	for(Expression i_exprs: exprs)
		i_exprs.writeXml(doc,tagEl,"",level+1);
}

	public void print(PrintStream os, int indent){
	os.println("ExprBody {");
	printIndent(os, indent+1);
	os.println("desc = '" + desc);
	printIndent(os, indent+1);
	os.println("exprs = <[");
	for(Expression i_exprs: exprs){
		printIndent(os, indent+2);
		i_exprs.print(os, indent+2);
	}
	printIndent(os, indent+1);
	os.println("]>");
	printIndent(os, indent+1);
	os.print("code = ");
	if (code == null)
		os.println("(null)");
	else
		code.print(os, indent+1);
	printIndent(os, indent);
	os.println("}");
}

	public String typeName(){
	return "ExprBody";
}

	public void printIndent(PrintStream os, int indent){
		for (int i=0; i<indent; i++) 
			os.print("  ");
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}

/********************************************************************************/

