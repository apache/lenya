<!-- ...................................................................... -->
<!-- Simplified DocBook Hierarchy V1.0 .................................... -->
<!-- File sdbhier.mod ..................................................... -->

<!-- Copyright 1992-2002 HaL Computer Systems, Inc.,
     O'Reilly & Associates, Inc., ArborText, Inc., Fujitsu Software
     Corporation, Norman Walsh, Sun Microsystems, Inc., and the
     Organization for the Advancement of Structured Information
     Standards (OASIS).

     $Id: sdbhier.mod,v 1.1 2003/04/02 12:35:52 andreas Exp $

     Permission to use, copy, modify and distribute the DocBook XML DTD
     and its accompanying documentation for any purpose and without fee
     is hereby granted in perpetuity, provided that the above copyright
     notice and this paragraph appear in all copies.  The copyright
     holders make no representation about the suitability of the DTD for
     any purpose.  It is provided "as is" without expressed or implied
     warranty.

     If you modify the Simplified DocBook DTD in any way, except for
     declaring and referencing additional sets of general entities and
     declaring additional notations, label your DTD as a variant of
     DocBook.  See the maintenance documentation for more information.

     Please direct all questions, bug reports, or suggestions for
     changes to the docbook@lists.oasis-open.org mailing list. For more
     information, see http://www.oasis-open.org/docbook/.
-->

<!ENTITY % local.divcomponent.mix "">
<!ENTITY % divcomponent.mix
		"%list.class;		|%admon.class;
		|%linespecific.class;
		|%para.class;		|%informal.class;
		|%formal.class;		|%compound.class;
					|%descobj.class;
		%local.divcomponent.mix;">

<!ENTITY % bookcomponent.content
	"((%divcomponent.mix;)+, section*)
	| section+">

<![ %include.refentry; [

<!ENTITY % local.refinline.char.mix "">
<!ENTITY % refinline.char.mix
		"#PCDATA
		|%xref.char.class;	|%gen.char.class;
		|%link.char.class;	|%tech.char.class;
		%local.refinline.char.mix;">

<!ENTITY % local.refcomponent.mix "">
<!ENTITY % refcomponent.mix
		"%list.class;		|%admon.class;
		|%linespecific.class;
		|%para.class;		|%informal.class;
		|%formal.class;		|%compound.class;
					|%descobj.class;
		%local.divcomponent.mix;">

<!ELEMENT refentry (refentryinfo?, refmeta?, (%link.char.class;)*,
                    refnamediv, refsynopsisdiv?, refsect1+)>

<!ELEMENT refentryinfo ((mediaobject | legalnotice
		| subjectset | keywordset
                | %bibliocomponent.mix;)+)>

<!ELEMENT refmeta (refentrytitle, manvolnum?, refmiscinfo*)>

<!ELEMENT refsect1info ((mediaobject | legalnotice
	| keywordset | subjectset | %bibliocomponent.mix;)+)>

<!ELEMENT refsect2info ((mediaobject | legalnotice
	| keywordset | subjectset | %bibliocomponent.mix;)+)>

<!ELEMENT refsect3info ((mediaobject | legalnotice
	| keywordset | subjectset | %bibliocomponent.mix;)+)>

<!ELEMENT refsynopsisdivinfo ((mediaobject | legalnotice
	| keywordset | subjectset | %bibliocomponent.mix;)+)>

<!ELEMENT refnamediv (refdescriptor?, refname+, refpurpose, refclass*,
		(%link.char.class;)*)>

]]>

<!-- End of Simplified DocBook Hierarchy V1.0 ............................. -->
<!-- ...................................................................... -->
