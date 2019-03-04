/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
 * 
 *  This file is part of AmapJ.
 *  
 *  AmapJ is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  AmapJ is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with AmapJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
 package fr.amapj.service.engine.generator.odt;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleBulletElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.odftoolkit.simple.text.list.ListItem;
import org.w3c.dom.Node;

/**
 * BulletDecorator is an implementation of the ListDecorator interface,
 * decorates a given List as bullet list. User can extend this class and realize
 * their own list and list item style. For example, set a specifies list item
 * with red color.
 * <p>
 * A BulletDecorator can be reused in the same Document.
 * 
 * @since 0.4
 */
public class AmapjBulletDecorator implements ListDecorator {

	private static String[] DEFAULT_TEXT_SPACE_BEFORE_ATTRIBUTES = { null, "0.401cm", "0.799cm", "1.2cm", "1.6cm",
			"2.001cm", "2.399cm", "2.8cm", "3.2cm", "3.601cm" };
	private static String DEFAULT_TEXT_MIN_LABEL_WIDTH = "0.4cm";
	private static String DEFAULT_FONT_NAME = "Arial";
	private static String DEFAULT_BULLET_CHAR = "•";
	private static String DEFAULT_NAME = "Ma-liste";

	private OdfTextListStyle listStyle;
	private OdfStyle paragraphStyle;
	private OdfOfficeAutomaticStyles styles;

	/**
	 * Constructor with Document.
	 * 
	 * @param doc
	 *            the Document which this BulletDecorator will be used on.
	 */
	public AmapjBulletDecorator(Document doc) {
		OdfContentDom contentDocument;
		try {
			contentDocument = doc.getContentDom();
			styles = contentDocument.getAutomaticStyles();
			OdfOfficeStyles documentStyles = doc.getDocumentStyles();
			listStyle = styles.getListStyle(DEFAULT_NAME);
			// create bullet style
			if (listStyle == null) {
				listStyle = styles.newListStyle();
				// <style:style style:name="Numbering_20_Symbols"
				// style:display-name="Numbering Symbols" style:family="text" />
				getOrCreateStyleByName(documentStyles, styles, "Bullet_20_Symbols", OdfStyleFamily.Text);
				for (int i = 0; i < 10; i++) {
					TextListLevelStyleBulletElement listLevelElement = listStyle.newTextListLevelStyleBulletElement(
							DEFAULT_BULLET_CHAR, i + 1);
					// get from default style element
					listLevelElement.setTextStyleNameAttribute("Bullet_20_Symbols");
					StyleListLevelPropertiesElement styleListLevelPropertiesElement = listLevelElement
							.newStyleListLevelPropertiesElement();
					if (DEFAULT_TEXT_SPACE_BEFORE_ATTRIBUTES[i] != null) {
						styleListLevelPropertiesElement
								.setTextSpaceBeforeAttribute(DEFAULT_TEXT_SPACE_BEFORE_ATTRIBUTES[i]);
					}
					styleListLevelPropertiesElement.setTextMinLabelWidthAttribute(DEFAULT_TEXT_MIN_LABEL_WIDTH);
					StyleTextPropertiesElement styleTextPropertiesElement = listLevelElement
							.newStyleTextPropertiesElement("true");
					styleTextPropertiesElement.setStyleFontNameAttribute(DEFAULT_FONT_NAME);
				}
				// listStyle.setStyleNameAttribute(DEFAULT_NAME);
			}
			// create default paragraph style
			// <style:style style:name="P3" style:family="paragraph"
			// style:parent-style-name="Default_20_Text"
			// style:list-style-name="L1"
			// />
			paragraphStyle = styles.newStyle(OdfStyleFamily.Paragraph);
			// <style:style style:name="Default_20_Text"
			// style:display-name="Default Text" style:family="paragraph"
			// style:class="text" />
			// <style:style style:name="Standard" style:family="paragraph"
			// style:class="text" />
			getOrCreateStyleByName(documentStyles, styles, "Default_20_Text", OdfStyleFamily.Paragraph);
			paragraphStyle.setStyleParentStyleNameAttribute("Default_20_Text");
			paragraphStyle.setStyleListStyleNameAttribute(listStyle.getStyleNameAttribute());
		} catch (Exception e) {
			Logger.getLogger(AmapjBulletDecorator.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public void decorateList(List list) {
		TextListElement listElement = list.getOdfElement();
		listElement.setTextStyleNameAttribute(listStyle.getStyleNameAttribute());
	}

	public void decorateListItem(ListItem item) {
		TextListItemElement listItemElement = item.getOdfElement();
		Node child = listItemElement.getFirstChild();
		while (child != null) {
			if (child instanceof TextPElement) {
				TextPElement pElement = (TextPElement) child;
				// user can realize defined style for specifies item.
				/*
				 * if (pElement.getTextContent().contains("item1")) { OdfStyle
				 * sParagraphStyle = styles.newStyle(OdfStyleFamily.Paragraph);
				 * sParagraphStyle
				 * .setStyleParentStyleNameAttribute("Default_20_Text");
				 * sParagraphStyle
				 * .newStyleTextPropertiesElement(null).setFoColorAttribute
				 * ("#ff3333");
				 * pElement.setTextStyleNameAttribute(sParagraphStyle
				 * .getStyleNameAttribute()); } else
				 */
				pElement.setTextStyleNameAttribute(paragraphStyle.getStyleNameAttribute());
			}
			child = child.getNextSibling();
		}
	}

	public ListType getListType() {
		return ListType.BULLET;
	}

	private OdfStyle getOrCreateStyleByName(OdfOfficeStyles documentStyles, OdfOfficeAutomaticStyles styles,
			String styleName, OdfStyleFamily styleFamily) {
		OdfStyle odfStyle = documentStyles.getStyle(styleName, styleFamily);
		if (odfStyle == null) {
			styles.getStyle(styleName, styleFamily);
		}
		if (odfStyle == null) {
			odfStyle = styles.newStyle(styleFamily);
			odfStyle.setStyleNameAttribute(styleName);
			odfStyle.setStyleDisplayNameAttribute(styleName);
		}
		return odfStyle;
	}
}
