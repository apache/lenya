BX_elements["para"] = new Array();
BX_elements["para"]["allowedElements"] = "emphasis | email | subscript | superscript | ulink | bold | itemizedlist | orderedlist | #PCDATA";
BX_elements["para"]["name"] = "Paragraph";
BX_elements["para"]["insertAfter"] = "titel | para | section";

BX_elements["emphasis"] = new Array();
BX_elements["emphasis"]["allowedElements"] = "ulink | bold | subscript | superscript | #PCDATA";
BX_elements["emphasis"]["name"] = "Kursiv";

BX_elements["bold"] = new Array();
BX_elements["bold"]["allowedElements"] = "ulink | emphasis | subscript | superscript | #PCDATA";
BX_elements["bold"]["name"] = "Fett";

BX_elements["subscript"] = new Array();
BX_elements["subscript"]["allowedElements"] = "#PCDATA";
BX_elements["subscript"]["name"] = "Tiefgestellt";

BX_elements["superscript"] = new Array();
BX_elements["superscript"]["allowedElements"] = "ulink | #PCDATA";
BX_elements["superscript"]["name"] = "Hochgestellt";

BX_elements["ulink"] = new Array();
BX_elements["ulink"]["allowedElements"] = "emphasis | #PCDATA";
BX_elements["ulink"]["name"] = "Link";
BX_elements["ulink"]["requiredAttributes"] = "url";
BX_elements["ulink"]["doTransform"] = 1;

BX_elements["email"] = new Array();
BX_elements["email"]["allowedElements"] = "emphasis | #PCDATA";
BX_elements["email"]["name"] = "Email";



BX_elements["titel"] = new Array();
BX_elements["titel"]["allowedElements"] = "#PCDATA | bold";
BX_elements["titel"]["returnElement"] = "para";
BX_elements["titel"]["name"] = "Titel";
BX_elements["titel"]["insertAfter"] = "para ";

BX_elements["entry"] = new Array();
BX_elements["entry"]["name"] = "cell";
BX_elements["entry"]["allowedElements"] = "bold | emphasis | #PCDATA | ulink | email";
BX_elements["entry"]["returnElement"] = "br";

BX_elements["title"] = new Array();
BX_elements["title"]["allowedElements"] = "#PCDATA";
BX_elements["title"]["name"] = "title";
BX_elements["title"]["returnElement"] = "none";
BX_elements["title"]["noAddParas"] = true;

BX_elements["articleinfo_author_name"] = new Array();
BX_elements["articleinfo_author_name"]["allowedElements"] = "#PCDATA";
BX_elements["articleinfo_author_name"]["name"] = "title";
BX_elements["articleinfo_author_name"]["returnElement"] = "none";
BX_elements["articleinfo_author_name"]["noAddParas"] = true;



BX_elements["section"] = new Array();
BX_elements["section"]["allowedElements"] = "section | para | titel | note";
BX_elements["section"]["name"] = "Section";
BX_elements["section"]["insertAfter"] = "section";
BX_elements["section"]["doTransform"] = 1;
BX_elements["section"]["addAlso"] = "para";



BX_elements["itemizedlist"] = new Array();
BX_elements["itemizedlist"]["allowedElements"] = "bxlistitem";
BX_elements["itemizedlist"]["name"] = "Itemized List";
BX_elements["itemizedlist"]["addAlso"] = "bxlistitem";
BX_elements["itemizedlist"]["doTransform"] = 1;
BX_elements["itemizedlist"]["replaceChildrenByAddAlso"] = "para";


BX_elements["orderedlist"] = new Array();
BX_elements["orderedlist"]["allowedElements"] = "bxlistitem";
BX_elements["orderedlist"]["name"] = "Ordered List";
BX_elements["orderedlist"]["addAlso"] = "bxlistitem";
BX_elements["orderedlist"]["doTransform"] = 1;

BX_elements["bxlistitem"] = new Array();
BX_elements["bxlistitem"]["allowedElements"] = "#PCDATA | bold | emphasis | subscript | superscript | orderedlist | itemizedlist";
BX_elements["bxlistitem"]["name"] = "Listelement";
BX_elements["bxlistitem"]["doTransform"] = 1;
BX_elements["bxlistitem"]["returnElement"] = "bxlistitem";
BX_elements["bxlistitem"]["afterEmptyLineNewElement"] = "#PCDATA";
BX_elements["bxlistitem"]["afterEmptyLineParent"] = "itemizedlist | orderedlist";

BX_elements["img"] = new Array();
BX_elements["img"]["allowedElements"] = "";
BX_elements["img"]["name"] = "Image";
BX_elements["img"]["altMenu"] = "BX_onContextMenuImg";

BX_elements["div"] = new Array();
BX_elements["div"]["allowedElements"] = "section |  i | p | b | ulink | #PCDATA";
BX_elements["div"]["name"] = "Root";

BX_elements["main"] = new Array();
BX_elements["main"]["allowedElements"] = "section";
BX_elements["main"]["name"] = "Main";
BX_elements["main"]["returnElement"] = "none";

