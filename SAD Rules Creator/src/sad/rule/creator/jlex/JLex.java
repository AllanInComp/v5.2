/***************************************************************
 JLEX COPYRIGHT NOTICE, LICENSE, AND DISCLAIMER
 Copyright 1996-2000 by Elliot Joel Berk and C. Scott Ananian 

 Permission to use, copy, modify, and distribute this software and its
 documentation for any purpose and without fee is hereby granted,
 provided that the above copyright notice appear in all copies and that
 both the copyright notice and this permission notice and warranty
 disclaimer appear in supporting documentation, and that the name of
 the authors or their employers not be used in advertising or publicity
 pertaining to distribution of the software without specific, written
 prior permission.

 The authors and their employers disclaim all warranties with regard to
 this software, including all implied warranties of merchantability and
 fitness. In no event shall the authors or their employers be liable
 for any special, indirect or consequential damages or any damages
 whatsoever resulting from loss of use, data or profits, whether in an
 action of contract, negligence or other tortious action, arising out
 of or in connection with the use or performance of this software.
 **************************************************************/

/***************************************************************
 Package Declaration
 **************************************************************/
package sad.rule.creator.jlex;

/***************************************************************
 Imported Packages
 **************************************************************/
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/******************************
 Questions:
 2) How should I use the Java package system
 to make my tool more modularized and
 coherent?

 Unimplemented:
 !) Fix BitSet issues -- expand only when necessary.
 2) Repeated accept rules.
 6) Clean up the CAlloc class and use buffered
 allocation.
 9) Add to spec about extending character set.
 11) m_verbose -- what should be done with it?
 12) turn lexical analyzer into a coherent
 Java package
 13) turn lexical analyzer generator into a
 coherent Java package
 16) pretty up generated code
 17) make it possible to have white space in
 regular expressions
 18) clean up all of the class files the lexer
 generator produces when it is compiled,
 and reduce this number in some way.
 24) character format to and from file: writeup
 and implementation
 25) Debug by testing all arcane regular expression cases.
 26) Look for and fix all UNDONE comments below.
 27) Fix package system.
 28) Clean up unnecessary classes.
 *****************************/

/***************************************************************
 Class: CAccept
 **************************************************************/
class CAccept {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	char m_action[];
	int m_action_read;
	int m_line_number;

	/***************************************************************
	 Function: CAccept
	 **************************************************************/
	CAccept(final CAccept accept) {
		int elem;

		this.m_action_read = accept.m_action_read;

		this.m_action = new char[this.m_action_read];
		for (elem = 0; elem < this.m_action_read; ++elem) {
			this.m_action[elem] = accept.m_action[elem];
		}

		this.m_line_number = accept.m_line_number;
	}

	/***************************************************************
	 Function: CAccept
	 **************************************************************/
	CAccept(final char action[], final int action_read, final int line_number) {
		int elem;

		this.m_action_read = action_read;

		this.m_action = new char[this.m_action_read];
		for (elem = 0; elem < this.m_action_read; ++elem) {
			this.m_action[elem] = action[elem];
		}

		this.m_line_number = line_number;
	}

	/***************************************************************
	 Function: mimic
	 **************************************************************/
	void mimic(final CAccept accept) {
		int elem;

		this.m_action_read = accept.m_action_read;

		this.m_action = new char[this.m_action_read];
		for (elem = 0; elem < this.m_action_read; ++elem) {
			this.m_action[elem] = accept.m_action[elem];
		}
	}
}

/***************************************************************
 Class: CAcceptAnchor
 **************************************************************/
class CAcceptAnchor {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	CAccept m_accept;
	int m_anchor;

	/***************************************************************
	 Function: CAcceptAnchor
	 **************************************************************/
	CAcceptAnchor() {
		this.m_accept = null;
		this.m_anchor = CSpec.NONE;
	}
}

/***************************************************************
 Class: CAlloc
 **************************************************************/
class CAlloc {
	/***************************************************************
	 Function: newCDfa
	 **************************************************************/
	static CDfa newCDfa(final CSpec spec) {
		CDfa dfa;

		dfa = new CDfa(spec.m_dfa_states.size());
		spec.m_dfa_states.addElement(dfa);

		return dfa;
	}

	/***************************************************************
	 Function: newCNfa
	 Description: 
	 **************************************************************/
	static CNfa newCNfa(final CSpec spec) {
		CNfa p;

		/* UNDONE: Buffer this? */

		p = new CNfa();

		/*p.m_label = spec.m_nfa_states.size();*/
		spec.m_nfa_states.addElement(p);
		p.m_edge = CNfa.EPSILON;

		return p;
	}
	/***************************************************************
	 Function: newCNfaPair
	 Description: 
	 **************************************************************/
	static CNfaPair newCNfaPair() {
		final CNfaPair pair = new CNfaPair();

		return pair;
	}

	/***************************************************************
	 Function: newNLPair
	 Description: return a new CNfaPair that matches a new 
	 line: (\r\n?|[\n\uu2028\uu2029])
	 Added by CSA 8-Aug-1999, updated 10-Aug-1999
	 **************************************************************/
	static CNfaPair newNLPair(final CSpec spec) {
		final CNfaPair pair = CAlloc.newCNfaPair();
		pair.m_end = CAlloc.newCNfa(spec); // newline accepting state
		pair.m_start = CAlloc.newCNfa(spec); // new state with two epsilon edges
		pair.m_start.m_next = CAlloc.newCNfa(spec);
		pair.m_start.m_next.m_edge = CNfa.CCL;
		pair.m_start.m_next.m_set = new CSet();
		pair.m_start.m_next.m_set.add('\n');
		if (spec.m_dtrans_ncols - CSpec.NUM_PSEUDO > 2029) {
			pair.m_start.m_next.m_set.add(2028); /*U+2028 is LS, the line separator*/
			pair.m_start.m_next.m_set.add(2029); /*U+2029 is PS, the paragraph sep.*/
		}
		pair.m_start.m_next.m_next = pair.m_end; // accept '\n', U+2028, or U+2029
		pair.m_start.m_next2 = CAlloc.newCNfa(spec);
		pair.m_start.m_next2.m_edge = '\r';
		pair.m_start.m_next2.m_next = CAlloc.newCNfa(spec);
		pair.m_start.m_next2.m_next.m_next = pair.m_end; // accept '\r';
		pair.m_start.m_next2.m_next.m_next2 = CAlloc.newCNfa(spec);
		pair.m_start.m_next2.m_next.m_next2.m_edge = '\n';
		pair.m_start.m_next2.m_next.m_next2.m_next = pair.m_end; // accept '\r\n';
		return pair;
	}
}

/***************************************************************
 Class: CBunch
 **************************************************************/
class CBunch {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	Vector m_nfa_set; /* Vector of CNfa states in dfa state. */
	SparseBitSet m_nfa_bit; /* BitSet representation of CNfa labels. */
	CAccept m_accept; /* Accepting actions, or null if nonaccepting state. */
	int m_anchor; /* Anchors on regular expression. */
	int m_accept_index; /* CNfa index corresponding to accepting actions. */

	/***************************************************************
	 Function: CBunch
	 Description: Constructor.
	 **************************************************************/
	CBunch() {
		this.m_nfa_set = null;
		this.m_nfa_bit = null;
		this.m_accept = null;
		this.m_anchor = CSpec.NONE;
		this.m_accept_index = -1;
	}
}

/***************************************************************
 Class: CDfa
 **************************************************************/
class CDfa {
	/***************************************************************
	 Member Variables
	 ***********************************************************/
	int m_group;
	boolean m_mark;
	CAccept m_accept;
	int m_anchor;
	Vector m_nfa_set;
	SparseBitSet m_nfa_bit;
	int m_label;

	/***************************************************************
	 Function: CDfa
	 **************************************************************/
	CDfa(final int label) {
		this.m_group = 0;
		this.m_mark = false;

		this.m_accept = null;
		this.m_anchor = CSpec.NONE;

		this.m_nfa_set = null;
		this.m_nfa_bit = null;

		this.m_label = label;
	}
}

/***************************************************************
 Class: CDTrans
 **************************************************************/
class CDTrans {
	/*************************************************************
	 Member Variables
	 ***********************************************************/
	int m_dtrans[];
	CAccept m_accept;
	int m_anchor;
	int m_label;

	/*************************************************************
	 Constants
	 ***********************************************************/
	static final int F = -1;

	/*************************************************************
	 Function: CTrans
	 ***********************************************************/
	CDTrans(final int label, final CSpec spec) {
		this.m_dtrans = new int[spec.m_dtrans_ncols];
		this.m_accept = null;
		this.m_anchor = CSpec.NONE;
		this.m_label = label;
	}
}

/***************************************************************
 Class: CEmit
 **************************************************************/
class CEmit {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	private CSpec m_spec;
	private java.io.PrintWriter m_outstream;

	private final boolean NOT_EDBG = false;

	/***************************************************************
	 Function: CEmit
	 Description: Constructor.
	 **************************************************************/
	CEmit() {
		this.reset();
	}

	/***************************************************************
	 Function: emit
	 Description: High-level access function to module.
	 **************************************************************/
	void emit(final CSpec spec, final java.io.PrintWriter outstream)
			throws java.io.IOException {
		this.set(spec, outstream);

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this.m_spec);
			CUtility.ASSERT(null != this.m_outstream);
		}

		if (CUtility.OLD_DEBUG) {
			this.print_details();
		}

		this.emit_header();
		this.emit_construct();
		this.emit_helpers();
		this.emit_driver();
		this.emit_footer();

		this.reset();
	}

	/***************************************************************
	 Function: emit_actions
	 Description:     
	 **************************************************************/
	private void emit_actions(final String tabs) throws java.io.IOException {
		int elem;
		int size;
		int bogus_index;
		CAccept accept;

		if (CUtility.DEBUG) {
			CUtility
				.ASSERT(this.m_spec.m_accept_vector.size() == this.m_spec.m_anchor_array.length);
		}

		bogus_index = -2;
		size = this.m_spec.m_accept_vector.size();
		for (elem = 0; elem < size; ++elem) {
			accept = (CAccept) this.m_spec.m_accept_vector.elementAt(elem);
			if (null != accept) {
				this.m_outstream.println(tabs + "case " + elem + ":");
				this.m_outstream.print(tabs + "\t");
				this.m_outstream.print(new String(
					accept.m_action,
					0,
					accept.m_action_read));
				this.m_outstream.println();
				this.m_outstream.println(tabs + "case " + bogus_index + ":");
				this.m_outstream.println(tabs + "\tbreak;");
				--bogus_index;
			}
		}
	}

	/***************************************************************
	 Function: emit_imports
	 Description: Emits import packages at top of 
	 generated source file.
	 **************************************************************/
	/*void emit_imports
	 (
	 CSpec spec,
	 OutputStream outstream
	 )
	 throws java.io.IOException      
	 {
	 set(spec,outstream);
	 
	 if (CUtility.DEBUG)
	 {
	 CUtility.ASSERT(null != m_spec);
	 CUtility.ASSERT(null != m_outstream);
	 }*/

	/*m_outstream.println("import java.lang.String;");
	 m_outstream.println("import java.lang.System;");
	 m_outstream.println("import java.io.BufferedReader;");
	 m_outstream.println("import java.io.InputStream;");*/
	/*  
	 reset();
	 }*/

	/***************************************************************
	 Function: emit_construct
	 Description: Emits constructor, member variables,
	 and constants.
	 **************************************************************/
	private void emit_construct() throws java.io.IOException {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this.m_spec);
			CUtility.ASSERT(null != this.m_outstream);
		}

		/* Constants */
		this.m_outstream.println("\tprivate final int YY_BUFFER_SIZE = 512;");

		this.m_outstream.println("\tprivate final int YY_F = -1;");
		this.m_outstream.println("\tprivate final int YY_NO_STATE = -1;");

		this.m_outstream.println("\tprivate final int YY_NOT_ACCEPT = 0;");
		this.m_outstream.println("\tprivate final int YY_START = 1;");
		this.m_outstream.println("\tprivate final int YY_END = 2;");
		this.m_outstream.println("\tprivate final int YY_NO_ANCHOR = 4;");

		// internal
		this.m_outstream.println("\tprivate final int YY_BOL = "
				+ this.m_spec.BOL + ";");
		this.m_outstream.println("\tprivate final int YY_EOF = "
				+ this.m_spec.EOF + ";");
		// external
		if (this.m_spec.m_integer_type || true == this.m_spec.m_yyeof) {
			this.m_outstream.println("\tpublic final int YYEOF = -1;");
		}

		/* User specified class code. */
		if (null != this.m_spec.m_class_code) {
			this.m_outstream.print(new String(
				this.m_spec.m_class_code,
				0,
				this.m_spec.m_class_read));
		}

		/* Member Variables */
		this.m_outstream.println("\tprivate java.io.BufferedReader yy_reader;");
		this.m_outstream.println("\tprivate int yy_buffer_index;");
		this.m_outstream.println("\tprivate int yy_buffer_read;");
		this.m_outstream.println("\tprivate int yy_buffer_start;");
		this.m_outstream.println("\tprivate int yy_buffer_end;");
		this.m_outstream.println("\tprivate char yy_buffer[];");
		if (this.m_spec.m_count_chars) {
			this.m_outstream.println("\tprivate int yychar;");
		}
		if (this.m_spec.m_count_lines) {
			this.m_outstream.println("\tprivate int yyline;");
		}
		this.m_outstream.println("\tprivate boolean yy_at_bol;");
		this.m_outstream.println("\tprivate int yy_lexical_state;");
		/*if (m_spec.m_count_lines || true == m_spec.m_count_chars)
		 {
		 m_outstream.println("\tprivate int yy_buffer_prev_start;");
		 }*/
		this.m_outstream.println();

		/* Function: first constructor (Reader) */
		this.m_outstream.print("\t");
		if (true == this.m_spec.m_public) {
			this.m_outstream.print("public ");
		}
		this.m_outstream.print(new String(this.m_spec.m_class_name));
		this.m_outstream.print(" (java.io.Reader reader)");

		if (null != this.m_spec.m_init_throw_code) {
			this.m_outstream.println();
			this.m_outstream.print("\t\tthrows ");
			this.m_outstream.print(new String(
				this.m_spec.m_init_throw_code,
				0,
				this.m_spec.m_init_throw_read));
			this.m_outstream.println();
			this.m_outstream.println("\t\t{");
		}
		else {
			this.m_outstream.println(" {");
		}

		this.m_outstream.println("\t\tthis ();");
		this.m_outstream.println("\t\tif (null == reader) {");
		this.m_outstream.println("\t\t\tthrow (new Error(\"Error: Bad input "
				+ "stream initializer.\"));");
		this.m_outstream.println("\t\t}");
		this.m_outstream
			.println("\t\tyy_reader = new java.io.BufferedReader(reader);");
		this.m_outstream.println("\t}");
		this.m_outstream.println();

		/* Function: second constructor (InputStream) */
		this.m_outstream.print("\t");
		if (true == this.m_spec.m_public) {
			this.m_outstream.print("public ");
		}
		this.m_outstream.print(new String(this.m_spec.m_class_name));
		this.m_outstream.print(" (java.io.InputStream instream)");

		if (null != this.m_spec.m_init_throw_code) {
			this.m_outstream.println();
			this.m_outstream.print("\t\tthrows ");
			this.m_outstream.println(new String(
				this.m_spec.m_init_throw_code,
				0,
				this.m_spec.m_init_throw_read));
			this.m_outstream.println("\t\t{");
		}
		else {
			this.m_outstream.println(" {");
		}

		this.m_outstream.println("\t\tthis ();");
		this.m_outstream.println("\t\tif (null == instream) {");
		this.m_outstream.println("\t\t\tthrow (new Error(\"Error: Bad input "
				+ "stream initializer.\"));");
		this.m_outstream.println("\t\t}");
		this.m_outstream
			.println("\t\tyy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));");
		this.m_outstream.println("\t}");
		this.m_outstream.println();

		/* Function: third, private constructor - only for internal use */
		this.m_outstream.print("\tprivate ");
		this.m_outstream.print(new String(this.m_spec.m_class_name));
		this.m_outstream.print(" ()");

		if (null != this.m_spec.m_init_throw_code) {
			this.m_outstream.println();
			this.m_outstream.print("\t\tthrows ");
			this.m_outstream.println(new String(
				this.m_spec.m_init_throw_code,
				0,
				this.m_spec.m_init_throw_read));
			this.m_outstream.println("\t\t{");
		}
		else {
			this.m_outstream.println(" {");
		}

		this.m_outstream.println("\t\tyy_buffer = new char[YY_BUFFER_SIZE];");
		this.m_outstream.println("\t\tyy_buffer_read = 0;");
		this.m_outstream.println("\t\tyy_buffer_index = 0;");
		this.m_outstream.println("\t\tyy_buffer_start = 0;");
		this.m_outstream.println("\t\tyy_buffer_end = 0;");
		if (this.m_spec.m_count_chars) {
			this.m_outstream.println("\t\tyychar = 0;");
		}
		if (this.m_spec.m_count_lines) {
			this.m_outstream.println("\t\tyyline = 0;");
		}
		this.m_outstream.println("\t\tyy_at_bol = true;");
		this.m_outstream.println("\t\tyy_lexical_state = YYINITIAL;");
		/*if (m_spec.m_count_lines || true == m_spec.m_count_chars)
		 {
		 m_outstream.println("\t\tyy_buffer_prev_start = 0;");
		 }*/

		/* User specified constructor code. */
		if (null != this.m_spec.m_init_code) {
			this.m_outstream.print(new String(
				this.m_spec.m_init_code,
				0,
				this.m_spec.m_init_read));
		}

		this.m_outstream.println("\t}");
		this.m_outstream.println();

	}

	/***************************************************************
	 Function: emit_driver
	 Description: 
	 **************************************************************/
	private void emit_driver() throws java.io.IOException {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this.m_spec);
			CUtility.ASSERT(null != this.m_outstream);
		}

		this.emit_table();

		if (this.m_spec.m_integer_type) {
			this.m_outstream.print("\tpublic int ");
			this.m_outstream.print(new String(this.m_spec.m_function_name));
			this.m_outstream.println(" ()");
		}
		else if (this.m_spec.m_intwrap_type) {
			this.m_outstream.print("\tpublic java.lang.Integer ");
			this.m_outstream.print(new String(this.m_spec.m_function_name));
			this.m_outstream.println(" ()");
		}
		else {
			this.m_outstream.print("\tpublic ");
			this.m_outstream.print(new String(this.m_spec.m_type_name));
			this.m_outstream.print(" ");
			this.m_outstream.print(new String(this.m_spec.m_function_name));
			this.m_outstream.println(" ()");
		}

		/*m_outstream.println("\t\tthrows java.io.IOException {");*/
		this.m_outstream.print("\t\tthrows java.io.IOException");
		if (null != this.m_spec.m_yylex_throw_code) {
			this.m_outstream.print(", ");
			this.m_outstream.print(new String(
				this.m_spec.m_yylex_throw_code,
				0,
				this.m_spec.m_yylex_throw_read));
			this.m_outstream.println();
			this.m_outstream.println("\t\t{");
		}
		else {
			this.m_outstream.println(" {");
		}

		this.m_outstream.println("\t\tint yy_lookahead;");
		this.m_outstream.println("\t\tint yy_anchor = YY_NO_ANCHOR;");
		/*m_outstream.println("\t\tint yy_state "
		 + "= yy_initial_dtrans(yy_lexical_state);");*/
		this.m_outstream.println("\t\tint yy_state "
				+ "= yy_state_dtrans[yy_lexical_state];");
		this.m_outstream.println("\t\tint yy_next_state = YY_NO_STATE;");
		/*m_outstream.println("\t\tint yy_prev_stave = YY_NO_STATE;");*/
		this.m_outstream.println("\t\tint yy_last_accept_state = YY_NO_STATE;");
		this.m_outstream.println("\t\tboolean yy_initial = true;");
		this.m_outstream.println("\t\tint yy_this_accept;");
		this.m_outstream.println();

		this.m_outstream.println("\t\tyy_mark_start();");
		/*m_outstream.println("\t\tyy_this_accept = yy_accept(yy_state);");*/
		this.m_outstream.println("\t\tyy_this_accept = yy_acpt[yy_state];");
		this.m_outstream.println("\t\tif (YY_NOT_ACCEPT != yy_this_accept) {");
		this.m_outstream.println("\t\t\tyy_last_accept_state = yy_state;");
		this.m_outstream.println("\t\t\tyy_mark_end();");
		this.m_outstream.println("\t\t}");

		if (this.NOT_EDBG) {
			this.m_outstream
				.println("\t\tjava.lang.System.out.println(\"Begin\");");
		}

		this.m_outstream.println("\t\twhile (true) {");

		this.m_outstream.println("\t\t\tif (yy_initial && yy_at_bol) "
				+ "yy_lookahead = YY_BOL;");
		this.m_outstream.println("\t\t\telse yy_lookahead = yy_advance();");
		this.m_outstream.println("\t\t\tyy_next_state = YY_F;");
		/*m_outstream.println("\t\t\t\tyy_next_state = "
		 + "yy_next(yy_state,yy_lookahead);");*/
		this.m_outstream.println("\t\t\tyy_next_state = "
				+ "yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];");

		if (this.NOT_EDBG) {
			this.m_outstream
				.println("java.lang.System.out.println(\"Current state: \""
						+ " + yy_state");
			this.m_outstream.println("+ \"\tCurrent input: \"");
			this.m_outstream.println(" + ((char) yy_lookahead));");
		}
		if (this.NOT_EDBG) {
			this.m_outstream
				.println("\t\t\tjava.lang.System.out.println(\"State = \""
						+ "+ yy_state);");
			this.m_outstream
				.println("\t\t\tjava.lang.System.out.println(\"Accepting status = \""
						+ "+ yy_this_accept);");
			this.m_outstream
				.println("\t\t\tjava.lang.System.out.println(\"Last accepting state = \""
						+ "+ yy_last_accept_state);");
			this.m_outstream
				.println("\t\t\tjava.lang.System.out.println(\"Next state = \""
						+ "+ yy_next_state);");
			this.m_outstream
				.println("\t\t\tjava.lang.System.out.println(\"Lookahead input = \""
						+ "+ ((char) yy_lookahead));");
		}

		// handle bare EOF.
		this.m_outstream.println("\t\t\tif (YY_EOF == yy_lookahead "
				+ "&& true == yy_initial) {");
		if (null != this.m_spec.m_eof_code) {
			this.m_outstream.println("\t\t\t\tyy_do_eof();");
		}
		if (true == this.m_spec.m_integer_type) {
			this.m_outstream.println("\t\t\t\treturn YYEOF;");
		}
		else if (null != this.m_spec.m_eof_value_code) {
			this.m_outstream.print(new String(
				this.m_spec.m_eof_value_code,
				0,
				this.m_spec.m_eof_value_read));
		}
		else {
			this.m_outstream.println("\t\t\t\treturn null;");
		}
		this.m_outstream.println("\t\t\t}");

		this.m_outstream.println("\t\t\tif (YY_F != yy_next_state) {");
		this.m_outstream.println("\t\t\t\tyy_state = yy_next_state;");
		this.m_outstream.println("\t\t\t\tyy_initial = false;");
		/*m_outstream.println("\t\t\t\tyy_this_accept = yy_accept(yy_state);");*/
		this.m_outstream.println("\t\t\t\tyy_this_accept = yy_acpt[yy_state];");
		this.m_outstream
			.println("\t\t\t\tif (YY_NOT_ACCEPT != yy_this_accept) {");
		this.m_outstream.println("\t\t\t\t\tyy_last_accept_state = yy_state;");
		this.m_outstream.println("\t\t\t\t\tyy_mark_end();");
		this.m_outstream.println("\t\t\t\t}");
		/*m_outstream.println("\t\t\t\tyy_prev_state = yy_state;");*/
		/*m_outstream.println("\t\t\t\tyy_state = yy_next_state;");*/
		this.m_outstream.println("\t\t\t}");

		this.m_outstream.println("\t\t\telse {");

		this.m_outstream
			.println("\t\t\t\tif (YY_NO_STATE == yy_last_accept_state) {");

		/*m_outstream.println("\t\t\t\t\tyy_error(YY_E_MATCH,false);");
		 m_outstream.println("\t\t\t\t\tyy_initial = true;");
		 m_outstream.println("\t\t\t\t\tyy_state "
		 + "= yy_state_dtrans[yy_lexical_state];");
		 m_outstream.println("\t\t\t\t\tyy_next_state = YY_NO_STATE;");*/
		/*m_outstream.println("\t\t\t\t\tyy_prev_state = YY_NO_STATE;");*/
		/*m_outstream.println("\t\t\t\t\tyy_last_accept_state = YY_NO_STATE;");
		 m_outstream.println("\t\t\t\t\tyy_mark_start();");*/
		/*m_outstream.println("\t\t\t\t\tyy_this_accept = yy_accept(yy_state);");*/
		/*m_outstream.println("\t\t\t\t\tyy_this_accept = yy_acpt[yy_state];");
		 m_outstream.println("\t\t\t\t\tif (YY_NOT_ACCEPT != yy_this_accept) {");
		 m_outstream.println("\t\t\t\t\t\tyy_last_accept_state = yy_state;");
		 m_outstream.println("\t\t\t\t\t}");*/

		this.m_outstream
			.println("\t\t\t\t\tthrow (new Error(\"Lexical Error: Unmatched Input.\"));");
		this.m_outstream.println("\t\t\t\t}");

		this.m_outstream.println("\t\t\t\telse {");

		this.m_outstream
			.println("\t\t\t\t\tyy_anchor = yy_acpt[yy_last_accept_state];");
		/*m_outstream.println("\t\t\t\t\tyy_anchor " 
		 + "= yy_accept(yy_last_accept_state);");*/
		this.m_outstream.println("\t\t\t\t\tif (0 != (YY_END & yy_anchor)) {");
		this.m_outstream.println("\t\t\t\t\t\tyy_move_end();");
		this.m_outstream.println("\t\t\t\t\t}");
		this.m_outstream.println("\t\t\t\t\tyy_to_mark();");

		this.m_outstream.println("\t\t\t\t\tswitch (yy_last_accept_state) {");

		this.emit_actions("\t\t\t\t\t");

		this.m_outstream.println("\t\t\t\t\tdefault:");
		this.m_outstream.println("\t\t\t\t\t\tyy_error(YY_E_INTERNAL,false);");
		/*m_outstream.println("\t\t\t\t\t\treturn null;");*/
		this.m_outstream.println("\t\t\t\t\tcase -1:");
		this.m_outstream.println("\t\t\t\t\t}");

		this.m_outstream.println("\t\t\t\t\tyy_initial = true;");
		this.m_outstream.println("\t\t\t\t\tyy_state "
				+ "= yy_state_dtrans[yy_lexical_state];");
		this.m_outstream.println("\t\t\t\t\tyy_next_state = YY_NO_STATE;");
		/*m_outstream.println("\t\t\t\t\tyy_prev_state = YY_NO_STATE;");*/
		this.m_outstream
			.println("\t\t\t\t\tyy_last_accept_state = YY_NO_STATE;");

		this.m_outstream.println("\t\t\t\t\tyy_mark_start();");

		/*m_outstream.println("\t\t\t\t\tyy_this_accept = yy_accept(yy_state);");*/
		this.m_outstream
			.println("\t\t\t\t\tyy_this_accept = yy_acpt[yy_state];");
		this.m_outstream
			.println("\t\t\t\t\tif (YY_NOT_ACCEPT != yy_this_accept) {");
		this.m_outstream
			.println("\t\t\t\t\t\tyy_last_accept_state = yy_state;");
		this.m_outstream.println("\t\t\t\t\t\tyy_mark_end();");
		this.m_outstream.println("\t\t\t\t\t}");

		this.m_outstream.println("\t\t\t\t}");
		this.m_outstream.println("\t\t\t}");
		this.m_outstream.println("\t\t}");
		this.m_outstream.println("\t}");

		/*m_outstream.println("\t\t\t\t");
		 m_outstream.println("\t\t\t");
		 m_outstream.println("\t\t\t");
		 m_outstream.println("\t\t\t");
		 m_outstream.println("\t\t\t");
		 m_outstream.println("\t\t}");*/
	}

	/***************************************************************
	 Function: emit_footer
	 Description:     
	 **************************************************************/
	private void emit_footer() throws java.io.IOException {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this.m_spec);
			CUtility.ASSERT(null != this.m_outstream);
		}

		this.m_outstream.println("}");
	}

	/***************************************************************
	 Function: emit_header
	 Description: Emits class header.
	 **************************************************************/
	private void emit_header() throws java.io.IOException {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this.m_spec);
			CUtility.ASSERT(null != this.m_outstream);
		}

		this.m_outstream.println();
		this.m_outstream.println();
		if (true == this.m_spec.m_public) {
			this.m_outstream.print("public ");
		}
		this.m_outstream.print("class ");
		this.m_outstream.print(new String(
			this.m_spec.m_class_name,
			0,
			this.m_spec.m_class_name.length));
		if (this.m_spec.m_implements_name.length > 0) {
			this.m_outstream.print(" implements ");
			this.m_outstream.print(new String(
				this.m_spec.m_implements_name,
				0,
				this.m_spec.m_implements_name.length));
		}
		this.m_outstream.println(" {");
	}

	/***************************************************************
	 Function: emit_helpers
	 Description: Emits helper functions, particularly 
	 error handling and input buffering.
	 **************************************************************/
	private void emit_helpers() throws java.io.IOException {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this.m_spec);
			CUtility.ASSERT(null != this.m_outstream);
		}

		/* Function: yy_do_eof */
		this.m_outstream.println("\tprivate boolean yy_eof_done = false;");
		if (null != this.m_spec.m_eof_code) {
			this.m_outstream.print("\tprivate void yy_do_eof ()");

			if (null != this.m_spec.m_eof_throw_code) {
				this.m_outstream.println();
				this.m_outstream.print("\t\tthrows ");
				this.m_outstream.println(new String(
					this.m_spec.m_eof_throw_code,
					0,
					this.m_spec.m_eof_throw_read));
				this.m_outstream.println("\t\t{");
			}
			else {
				this.m_outstream.println(" {");
			}

			this.m_outstream.println("\t\tif (false == yy_eof_done) {");
			this.m_outstream.print(new String(
				this.m_spec.m_eof_code,
				0,
				this.m_spec.m_eof_read));
			this.m_outstream.println("\t\t}");
			this.m_outstream.println("\t\tyy_eof_done = true;");
			this.m_outstream.println("\t}");
		}

		this.emit_states();

		/* Function: yybegin */
		this.m_outstream.println("\tprivate void yybegin (int state) {");
		this.m_outstream.println("\t\tyy_lexical_state = state;");
		this.m_outstream.println("\t}");

		/* Function: yy_initial_dtrans */
		/*m_outstream.println("\tprivate int yy_initial_dtrans (int state) {");
		 m_outstream.println("\t\treturn yy_state_dtrans[state];");
		 m_outstream.println("\t}");*/

		/* Function: yy_advance */
		this.m_outstream.println("\tprivate int yy_advance ()");
		this.m_outstream.println("\t\tthrows java.io.IOException {");
		/*m_outstream.println("\t\t{");*/
		this.m_outstream.println("\t\tint next_read;");
		this.m_outstream.println("\t\tint i;");
		this.m_outstream.println("\t\tint j;");
		this.m_outstream.println();

		this.m_outstream.println("\t\tif (yy_buffer_index < yy_buffer_read) {");
		this.m_outstream.println("\t\t\treturn yy_buffer[yy_buffer_index++];");
		/*m_outstream.println("\t\t\t++yy_buffer_index;");*/
		this.m_outstream.println("\t\t}");
		this.m_outstream.println();

		this.m_outstream.println("\t\tif (0 != yy_buffer_start) {");
		this.m_outstream.println("\t\t\ti = yy_buffer_start;");
		this.m_outstream.println("\t\t\tj = 0;");
		this.m_outstream.println("\t\t\twhile (i < yy_buffer_read) {");
		this.m_outstream.println("\t\t\t\tyy_buffer[j] = yy_buffer[i];");
		this.m_outstream.println("\t\t\t\t++i;");
		this.m_outstream.println("\t\t\t\t++j;");
		this.m_outstream.println("\t\t\t}");
		this.m_outstream
			.println("\t\t\tyy_buffer_end = yy_buffer_end - yy_buffer_start;");
		this.m_outstream.println("\t\t\tyy_buffer_start = 0;");
		this.m_outstream.println("\t\t\tyy_buffer_read = j;");
		this.m_outstream.println("\t\t\tyy_buffer_index = j;");
		this.m_outstream.println("\t\t\tnext_read = yy_reader.read(yy_buffer,");
		this.m_outstream.println("\t\t\t\t\tyy_buffer_read,");
		this.m_outstream
			.println("\t\t\t\t\tyy_buffer.length - yy_buffer_read);");
		this.m_outstream.println("\t\t\tif (-1 == next_read) {");
		this.m_outstream.println("\t\t\t\treturn YY_EOF;");
		this.m_outstream.println("\t\t\t}");
		this.m_outstream
			.println("\t\t\tyy_buffer_read = yy_buffer_read + next_read;");
		this.m_outstream.println("\t\t}");
		this.m_outstream.println();

		this.m_outstream
			.println("\t\twhile (yy_buffer_index >= yy_buffer_read) {");
		this.m_outstream
			.println("\t\t\tif (yy_buffer_index >= yy_buffer.length) {");
		this.m_outstream.println("\t\t\t\tyy_buffer = yy_double(yy_buffer);");
		this.m_outstream.println("\t\t\t}");
		this.m_outstream.println("\t\t\tnext_read = yy_reader.read(yy_buffer,");
		this.m_outstream.println("\t\t\t\t\tyy_buffer_read,");
		this.m_outstream
			.println("\t\t\t\t\tyy_buffer.length - yy_buffer_read);");
		this.m_outstream.println("\t\t\tif (-1 == next_read) {");
		this.m_outstream.println("\t\t\t\treturn YY_EOF;");
		this.m_outstream.println("\t\t\t}");
		this.m_outstream
			.println("\t\t\tyy_buffer_read = yy_buffer_read + next_read;");
		this.m_outstream.println("\t\t}");

		this.m_outstream.println("\t\treturn yy_buffer[yy_buffer_index++];");
		this.m_outstream.println("\t}");

		/* Function: yy_move_end */
		this.m_outstream.println("\tprivate void yy_move_end () {");
		this.m_outstream.println("\t\tif (yy_buffer_end > yy_buffer_start &&");
		this.m_outstream
			.println("\t\t    '\\n' == yy_buffer[yy_buffer_end-1])");
		this.m_outstream.println("\t\t\tyy_buffer_end--;");
		this.m_outstream.println("\t\tif (yy_buffer_end > yy_buffer_start &&");
		this.m_outstream
			.println("\t\t    '\\r' == yy_buffer[yy_buffer_end-1])");
		this.m_outstream.println("\t\t\tyy_buffer_end--;");
		this.m_outstream.println("\t}");

		/* Function: yy_mark_start */
		this.m_outstream.println("\tprivate boolean yy_last_was_cr=false;");
		this.m_outstream.println("\tprivate void yy_mark_start () {");
		if (this.m_spec.m_count_lines || true == this.m_spec.m_count_chars) {
			if (this.m_spec.m_count_lines) {
				this.m_outstream.println("\t\tint i;");
				this.m_outstream.println("\t\tfor (i = yy_buffer_start; "
						+ "i < yy_buffer_index; ++i) {");
				this.m_outstream
					.println("\t\t\tif ('\\n' == yy_buffer[i] && !yy_last_was_cr) {");
				this.m_outstream.println("\t\t\t\t++yyline;");
				this.m_outstream.println("\t\t\t}");
				this.m_outstream.println("\t\t\tif ('\\r' == yy_buffer[i]) {");
				this.m_outstream.println("\t\t\t\t++yyline;");
				this.m_outstream.println("\t\t\t\tyy_last_was_cr=true;");
				this.m_outstream.println("\t\t\t} else yy_last_was_cr=false;");
				this.m_outstream.println("\t\t}");
			}
			if (this.m_spec.m_count_chars) {
				this.m_outstream.println("\t\tyychar = yychar");
				this.m_outstream
					.println("\t\t\t+ yy_buffer_index - yy_buffer_start;");
			}
		}
		this.m_outstream.println("\t\tyy_buffer_start = yy_buffer_index;");
		this.m_outstream.println("\t}");

		/* Function: yy_mark_end */
		this.m_outstream.println("\tprivate void yy_mark_end () {");
		this.m_outstream.println("\t\tyy_buffer_end = yy_buffer_index;");
		this.m_outstream.println("\t}");

		/* Function: yy_to_mark */
		this.m_outstream.println("\tprivate void yy_to_mark () {");
		this.m_outstream.println("\t\tyy_buffer_index = yy_buffer_end;");
		this.m_outstream.println("\t\tyy_at_bol = "
				+ "(yy_buffer_end > yy_buffer_start) &&");
		this.m_outstream.println("\t\t            "
				+ "('\\r' == yy_buffer[yy_buffer_end-1] ||");
		this.m_outstream.println("\t\t            "
				+ " '\\n' == yy_buffer[yy_buffer_end-1] ||");
		this.m_outstream.println("\t\t            " + /* unicode LS */
		" 2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||");
		this.m_outstream.println("\t\t            " + /* unicode PS */
		" 2029/*PS*/ == yy_buffer[yy_buffer_end-1]);");
		this.m_outstream.println("\t}");

		/* Function: yytext */
		this.m_outstream.println("\tprivate java.lang.String yytext () {");
		this.m_outstream.println("\t\treturn (new java.lang.String(yy_buffer,");
		this.m_outstream.println("\t\t\tyy_buffer_start,");
		this.m_outstream.println("\t\t\tyy_buffer_end - yy_buffer_start));");
		this.m_outstream.println("\t}");

		/* Function: yylength */
		this.m_outstream.println("\tprivate int yylength () {");
		this.m_outstream.println("\t\treturn yy_buffer_end - yy_buffer_start;");
		this.m_outstream.println("\t}");

		/* Function: yy_double */
		this.m_outstream.println("\tprivate char[] yy_double (char buf[]) {");
		this.m_outstream.println("\t\tint i;");
		this.m_outstream.println("\t\tchar newbuf[];");
		this.m_outstream.println("\t\tnewbuf = new char[2*buf.length];");
		this.m_outstream.println("\t\tfor (i = 0; i < buf.length; ++i) {");
		this.m_outstream.println("\t\t\tnewbuf[i] = buf[i];");
		this.m_outstream.println("\t\t}");
		this.m_outstream.println("\t\treturn newbuf;");
		this.m_outstream.println("\t}");

		/* Function: yy_error */
		this.m_outstream.println("\tprivate final int YY_E_INTERNAL = 0;");
		this.m_outstream.println("\tprivate final int YY_E_MATCH = 1;");
		this.m_outstream
			.println("\tprivate java.lang.String yy_error_string[] = {");
		this.m_outstream.println("\t\t\"Error: Internal error.\\n\",");
		this.m_outstream.println("\t\t\"Error: Unmatched input.\\n\"");
		this.m_outstream.println("\t};");
		this.m_outstream
			.println("\tprivate void yy_error (int code,boolean fatal) {");
		this.m_outstream
			.println("\t\tjava.lang.System.out.print(yy_error_string[code]);");
		this.m_outstream.println("\t\tjava.lang.System.out.flush();");
		this.m_outstream.println("\t\tif (fatal) {");
		this.m_outstream.println("\t\t\tthrow new Error(\"Fatal Error.\\n\");");
		this.m_outstream.println("\t\t}");
		this.m_outstream.println("\t}");

		/* Function: yy_next */
		/*m_outstream.println("\tprivate int yy_next (int current,char lookahead) {");
		 m_outstream.println("\t\treturn yy_nxt[yy_rmap[current]][yy_cmap[lookahead]];");
		 m_outstream.println("\t}");*/

		/* Function: yy_accept */
		/*m_outstream.println("\tprivate int yy_accept (int current) {");
		 m_outstream.println("\t\treturn yy_acpt[current];");
		 m_outstream.println("\t}");*/

		// Function: private int [][] unpackFromString(int size1, int size2, String st)
		// Added 6/24/98 Raimondas Lencevicius
		// May be made more efficient by replacing String operations
		// Assumes correctly formed input String. Performs no error checking
		this.m_outstream.println("\tprivate int[][] unpackFromString"
				+ "(int size1, int size2, String st) {");
		this.m_outstream.println("\t\tint colonIndex = -1;");
		this.m_outstream.println("\t\tString lengthString;");
		this.m_outstream.println("\t\tint sequenceLength = 0;");
		this.m_outstream.println("\t\tint sequenceInteger = 0;");
		this.m_outstream.println();
		this.m_outstream.println("\t\tint commaIndex;");
		this.m_outstream.println("\t\tString workString;");
		this.m_outstream.println();
		this.m_outstream.println("\t\tint res[][] = new int[size1][size2];");
		this.m_outstream.println("\t\tfor (int i= 0; i < size1; i++) {");
		this.m_outstream.println("\t\t\tfor (int j= 0; j < size2; j++) {");
		this.m_outstream.println("\t\t\t\tif (sequenceLength != 0) {");
		this.m_outstream.println("\t\t\t\t\tres[i][j] = sequenceInteger;");
		this.m_outstream.println("\t\t\t\t\tsequenceLength--;");
		this.m_outstream.println("\t\t\t\t\tcontinue;");
		this.m_outstream.println("\t\t\t\t}");
		this.m_outstream.println("\t\t\t\tcommaIndex = st.indexOf(',');");
		this.m_outstream
			.println("\t\t\t\tworkString = (commaIndex==-1) ? st :");
		this.m_outstream.println("\t\t\t\t\tst.substring(0, commaIndex);");
		this.m_outstream.println("\t\t\t\tst = st.substring(commaIndex+1);");
		this.m_outstream
			.println("\t\t\t\tcolonIndex = workString.indexOf(':');");
		this.m_outstream.println("\t\t\t\tif (colonIndex == -1) {");
		this.m_outstream
			.println("\t\t\t\t\tres[i][j]=Integer.parseInt(workString);");
		this.m_outstream.println("\t\t\t\t\tcontinue;");
		this.m_outstream.println("\t\t\t\t}");
		this.m_outstream.println("\t\t\t\tlengthString =");
		this.m_outstream
			.println("\t\t\t\t\tworkString.substring(colonIndex+1);");
		this.m_outstream.println("\t\t\t\tsequenceLength="
				+ "Integer.parseInt(lengthString);");
		this.m_outstream.println("\t\t\t\tworkString="
				+ "workString.substring(0,colonIndex);");
		this.m_outstream.println("\t\t\t\tsequenceInteger="
				+ "Integer.parseInt(workString);");
		this.m_outstream.println("\t\t\t\tres[i][j] = sequenceInteger;");
		this.m_outstream.println("\t\t\t\tsequenceLength--;");
		this.m_outstream.println("\t\t\t}");
		this.m_outstream.println("\t\t}");
		this.m_outstream.println("\t\treturn res;");
		this.m_outstream.println("\t}");
	}

	/***************************************************************
	 Function: emit_states
	 Description: Emits constants that serve as lexical states,
	 including YYINITIAL.
	 **************************************************************/
	private void emit_states() throws java.io.IOException {
		Enumeration states;
		String state;
		int index;

		states = this.m_spec.m_states.keys();
		/*index = 0;*/
		while (states.hasMoreElements()) {
			state = (String) states.nextElement();

			if (CUtility.DEBUG) {
				CUtility.ASSERT(null != state);
			}

			this.m_outstream.println("\tprivate final int " + state + " = "
					+ this.m_spec.m_states.get(state).toString() + ";");
			/*++index;*/
		}

		this.m_outstream.println("\tprivate final int yy_state_dtrans[] = {");
		for (index = 0; index < this.m_spec.m_state_dtrans.length; ++index) {
			this.m_outstream.print("\t\t" + this.m_spec.m_state_dtrans[index]);
			if (index < this.m_spec.m_state_dtrans.length - 1) {
				this.m_outstream.println(",");
			}
			else {
				this.m_outstream.println();
			}
		}
		this.m_outstream.println("\t};");
	}

	/***************************************************************
	 Function: emit_table
	 Description: Emits transition table.
	 **************************************************************/
	private void emit_table() throws java.io.IOException {
		int i;
		int elem;
		int size;
		CDTrans dtrans;
		boolean is_start;
		boolean is_end;
		CAccept accept;

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this.m_spec);
			CUtility.ASSERT(null != this.m_outstream);
		}

		this.m_outstream.println("\tprivate int yy_acpt[] = {");
		size = this.m_spec.m_accept_vector.size();
		for (elem = 0; elem < size; ++elem) {
			accept = (CAccept) this.m_spec.m_accept_vector.elementAt(elem);

			this.m_outstream.print("\t\t/* " + elem + " */ ");
			if (null != accept) {
				is_start =
					0 != (this.m_spec.m_anchor_array[elem] & CSpec.START);
				is_end = 0 != (this.m_spec.m_anchor_array[elem] & CSpec.END);

				if (is_start && true == is_end) {
					this.m_outstream.print("YY_START | YY_END");
				}
				else if (is_start) {
					this.m_outstream.print("YY_START");
				}
				else if (is_end) {
					this.m_outstream.print("YY_END");
				}
				else {
					this.m_outstream.print("YY_NO_ANCHOR");
				}
			}
			else {
				this.m_outstream.print("YY_NOT_ACCEPT");
			}

			if (elem < size - 1) {
				this.m_outstream.print(",");
			}

			this.m_outstream.println();
		}
		this.m_outstream.println("\t};");

		// CSA: modified yy_cmap to use string packing 9-Aug-1999
		final int[] yy_cmap = new int[this.m_spec.m_ccls_map.length];
		for (i = 0; i < this.m_spec.m_ccls_map.length; ++i) {
			yy_cmap[i] = this.m_spec.m_col_map[this.m_spec.m_ccls_map[i]];
		}
		this.m_outstream.print("\tprivate int yy_cmap[] = unpackFromString(");
		this.emit_table_as_string(new int[][] { yy_cmap });
		this.m_outstream.println(")[0];");
		this.m_outstream.println();

		// CSA: modified yy_rmap to use string packing 9-Aug-1999
		this.m_outstream.print("\tprivate int yy_rmap[] = unpackFromString(");
		this.emit_table_as_string(new int[][] { this.m_spec.m_row_map });
		this.m_outstream.println(")[0];");
		this.m_outstream.println();

		// 6/24/98 Raimondas Lencevicius
		// modified to use
		//    int[][] unpackFromString(int size1, int size2, String st)
		size = this.m_spec.m_dtrans_vector.size();
		final int[][] yy_nxt = new int[size][];
		for (elem = 0; elem < size; elem++) {
			dtrans = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(elem);
			CUtility
				.ASSERT(dtrans.m_dtrans.length == this.m_spec.m_dtrans_ncols);
			yy_nxt[elem] = dtrans.m_dtrans;
		}
		this.m_outstream.print("\tprivate int yy_nxt[][] = unpackFromString(");
		this.emit_table_as_string(yy_nxt);
		this.m_outstream.println(");");
		this.m_outstream.println();
	}

	/***************************************************************
	 Function: emit_driver
	 Description: Output an integer table as a string.  Written by
	 Raimondas Lencevicius 6/24/98; reorganized by CSA 9-Aug-1999.
	 From his original comments:
	 yy_nxt[][] values are coded into a string
	 by printing integers and representing
	 integer sequences as "value:length" pairs.
	 **************************************************************/
	private void emit_table_as_string(final int[][] ia) {
		int sequenceLength = 0; // RL - length of the number sequence
		boolean sequenceStarted = false; // RL - has number sequence started?
		int previousInt = -20; // RL - Bogus -20 state.

		// RL - Output matrix size
		this.m_outstream.print(ia.length);
		this.m_outstream.print(",");
		this.m_outstream.print(ia.length > 0 ? ia[0].length : 0);
		this.m_outstream.println(",");

		StringBuffer outstr = new StringBuffer();

		//  RL - Output matrix 
		for (int elem = 0; elem < ia.length; ++elem) {
			for (int i = 0; i < ia[elem].length; ++i) {
				final int writeInt = ia[elem][i];
				if (writeInt == previousInt) // RL - sequence?
				{
					if (sequenceStarted) {
						sequenceLength++;
					}
					else {
						outstr.append(writeInt);
						outstr.append(":");
						sequenceLength = 2;
						sequenceStarted = true;
					}
				}
				else // RL - no sequence or end sequence
				{
					if (sequenceStarted) {
						outstr.append(sequenceLength);
						outstr.append(",");
						sequenceLength = 0;
						sequenceStarted = false;
					}
					else {
						if (previousInt != -20) {
							outstr.append(previousInt);
							outstr.append(",");
						}
					}
				}
				previousInt = writeInt;
				// CSA: output in 75 character chunks.
				if (outstr.length() > 75) {
					final String s = outstr.toString();
					this.m_outstream
						.println("\"" + s.substring(0, 75) + "\" +");
					outstr = new StringBuffer(s.substring(75));
				}
			}
		}
		if (sequenceStarted) {
			outstr.append(sequenceLength);
		}
		else {
			outstr.append(previousInt);
		}
		// CSA: output in 75 character chunks.
		if (outstr.length() > 75) {
			final String s = outstr.toString();
			this.m_outstream.println("\"" + s.substring(0, 75) + "\" +");
			outstr = new StringBuffer(s.substring(75));
		}
		this.m_outstream.print("\"" + outstr + "\"");
	}

	/***************************************************************
	 Function: print_details
	 Description: Debugging output.
	 **************************************************************/
	private void print_details() {
		int i;
		int j;
		int next;
		int state;
		CDTrans dtrans;
		CAccept accept;
		boolean tr;

		System.out.println("---------------------- Transition Table "
				+ "----------------------");

		for (i = 0; i < this.m_spec.m_row_map.length; ++i) {
			System.out.print("State " + i);

			accept = (CAccept) this.m_spec.m_accept_vector.elementAt(i);
			if (null == accept) {
				System.out.println(" [nonaccepting]");
			}
			else {
				System.out.println(" [accepting, line "
						+ accept.m_line_number
						+ " <"
						+ new java.lang.String(
							accept.m_action,
							0,
							accept.m_action_read) + ">]");
			}
			dtrans =
				(CDTrans) this.m_spec.m_dtrans_vector
					.elementAt(this.m_spec.m_row_map[i]);

			tr = false;
			state = dtrans.m_dtrans[this.m_spec.m_col_map[0]];
			if (CDTrans.F != state) {
				tr = true;
				System.out.print("\tgoto " + state + " on [" + (char) 0);
			}
			for (j = 1; j < this.m_spec.m_dtrans_ncols; ++j) {
				next = dtrans.m_dtrans[this.m_spec.m_col_map[j]];
				if (state == next) {
					if (CDTrans.F != state) {
						System.out.print((char) j);
					}
				}
				else {
					state = next;
					if (tr) {
						System.out.println("]");
						tr = false;
					}
					if (CDTrans.F != state) {
						tr = true;
						System.out
							.print("\tgoto " + state + " on [" + (char) j);
					}
				}
			}
			if (tr) {
				System.out.println("]");
			}
		}

		System.out.println("---------------------- Transition Table "
				+ "----------------------");
	}

	/***************************************************************
	 Function: reset
	 Description: Clears member variables.
	 **************************************************************/
	private void reset() {
		this.m_spec = null;
		this.m_outstream = null;
	}

	/***************************************************************
	 Function: set
	 Description: Initializes member variables.
	 **************************************************************/
	private void set(final CSpec spec, final java.io.PrintWriter outstream) {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != spec);
			CUtility.ASSERT(null != outstream);
		}

		this.m_spec = spec;
		this.m_outstream = outstream;
	}
}

/********************************************************
 Class: CError
 *******************************************************/
class CError {
	/********************************************************
	 Constants
	 Description: Error codes for parse_error().
	 *******************************************************/
	static final int E_BADEXPR = 0;

	static final int E_PAREN = 1;
	static final int E_LENGTH = 2;
	static final int E_BRACKET = 3;
	static final int E_BOL = 4;
	static final int E_CLOSE = 5;
	static final int E_NEWLINE = 6;
	static final int E_BADMAC = 7;
	static final int E_NOMAC = 8;
	static final int E_MACDEPTH = 9;
	static final int E_INIT = 10;
	static final int E_EOF = 11;
	static final int E_DIRECT = 12;
	static final int E_INTERNAL = 13;
	static final int E_STATE = 14;
	static final int E_MACDEF = 15;
	static final int E_SYNTAX = 16;
	static final int E_BRACE = 17;
	static final int E_DASH = 18;
	static final int E_ZERO = 19;
	static final int E_BADCTRL = 20;
	/********************************************************
	 Constants
	 Description: String messages for parse_error();
	 *******************************************************/
	static final String errmsg[] = {
			"Malformed regular expression.",
			"Missing close parenthesis.",
			"Too many regular expressions or expression too long.",
			"Missing [ in character class.",
			"^ must be at start of expression or after [.",
			"+ ? or * must follow an expression or subexpression.",
			"Newline in quoted string.",
			"Missing } in macro expansion.",
			"Macro does not exist.",
			"Macro expansions nested too deeply.",
			"JLex has not been successfully initialized.",
			"Unexpected end-of-file found.",
			"Undefined or badly-formed JLex directive.",
			"Internal JLex error.",
			"Unitialized state name.",
			"Badly formed macro definition.",
			"Syntax error.",
			"Missing brace at start of lexical action.",
			"Special character dash - in character class [...] must\n"
					+ "\tbe preceded by start-of-range character.",
			"Zero-length regular expression.",
			"Illegal \\^C-style escape sequence (character following caret must\n"
					+ "\tbe alphabetic).", };

	/********************************************************
	 Function: impos
	 Description:
	 *******************************************************/
	static void impos(final String message) {
		System.out.println("JLex Error: " + message);
	}

	/********************************************************
	 Function: parse_error
	 Description:
	 *******************************************************/
	static void parse_error(final int error_code, final int line_number) {
		System.out.println("Error: Parse error at line " + line_number + ".");
		System.out.println("Description: " + CError.errmsg[error_code]);
		throw new Error("Parse error.");
	}
}

/***************************************************************
 Class: CInput
 Description: 
 **************************************************************/
class CInput {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	private final java.io.BufferedReader m_input; /* JLex specification file. */

	boolean m_eof_reached; /* Whether EOF has been encountered. */
	boolean m_pushback_line;

	char m_line[]; /* Line buffer. */
	int m_line_read; /* Number of bytes read into line buffer. */
	int m_line_index; /* Current index into line buffer. */

	int m_line_number; /* Current line number. */

	/***************************************************************
	 Constants
	 **************************************************************/
	static final boolean EOF = true;
	static final boolean NOT_EOF = false;

	/***************************************************************
	 Function: CInput
	 Description: 
	 **************************************************************/
	CInput(final java.io.Reader input) {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != input);
		}

		/* Initialize input stream. */
		this.m_input = new java.io.BufferedReader(input);

		/* Initialize buffers and index counters. */
		this.m_line = null;
		this.m_line_read = 0;
		this.m_line_index = 0;

		/* Initialize state variables. */
		this.m_eof_reached = false;
		this.m_line_number = 0;
		this.m_pushback_line = false;
	}

	/***************************************************************
	 Function: getLine
	 Description: Returns true on EOF, false otherwise.
	 Guarantees not to return a blank line, or a line
	 of zero length.
	 **************************************************************/
	boolean getLine() throws java.io.IOException {
		String lineStr;
		int elem;

		/* Has EOF already been reached? */
		if (this.m_eof_reached) {
			return CInput.EOF;
		}

		/* Pushback current line? */
		if (this.m_pushback_line) {
			this.m_pushback_line = false;

			/* Check for empty line. */
			for (elem = 0; elem < this.m_line_read; ++elem) {
				if (false == CUtility.isspace(this.m_line[elem])) {
					break;
				}
			}

			/* Nonempty? */
			if (elem < this.m_line_read) {
				this.m_line_index = 0;
				return CInput.NOT_EOF;
			}
		}

		while (true) {
			if (null == (lineStr = this.m_input.readLine())) {
				this.m_eof_reached = true;
				this.m_line_index = 0;
				return CInput.EOF;
			}
			this.m_line = (lineStr + "\n").toCharArray();
			this.m_line_read = this.m_line.length;
			++this.m_line_number;

			/* Check for empty lines and discard them. */
			elem = 0;
			while (CUtility.isspace(this.m_line[elem])) {
				++elem;
				if (elem == this.m_line_read) {
					break;
				}
			}

			if (elem < this.m_line_read) {
				break;
			}
		}

		this.m_line_index = 0;
		return CInput.NOT_EOF;
	}
}

/***************************************************************
 Class: CLexGen
 **************************************************************/
class CLexGen {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	private java.io.Reader m_instream; /* JLex specification file. */
	private java.io.PrintWriter m_outstream; /* Lexical analyzer source file. */

	private CInput m_input; /* Input buffer class. */

	private Hashtable m_tokens; /* Hashtable that maps characters to their 
								corresponding lexical code for
								the internal lexical analyzer. */
	private CSpec m_spec; /* Spec class holds information
							about the generated lexer. */
	private boolean m_init_flag; /* Flag set to true only upon 
									successful initialization. */

	private CMakeNfa m_makeNfa; /* NFA machine generator module. */
	private CNfa2Dfa m_nfa2dfa; /* NFA to DFA machine (transition table) 
								conversion module. */
	private CMinimize m_minimize; /* Transition table compressor. */
	private CSimplifyNfa m_simplifyNfa; /* NFA simplifier using char classes */
	private CEmit m_emit; /* Output module that emits source code
							into the generated lexer file. */

	/********************************************************
	 Constants
	 *******************************************************/
	private static final boolean ERROR = false;
	private static final boolean NOT_ERROR = true;
	private static final int BUFFER_SIZE = 1024;

	/********************************************************
	 Constants: Token Types
	 *******************************************************/
	static final int EOS = 1;
	static final int ANY = 2;
	static final int AT_BOL = 3;
	static final int AT_EOL = 4;
	static final int CCL_END = 5;
	static final int CCL_START = 6;
	static final int CLOSE_CURLY = 7;
	static final int CLOSE_PAREN = 8;
	static final int CLOSURE = 9;
	static final int DASH = 10;
	static final int END_OF_INPUT = 11;
	static final int L = 12;
	static final int OPEN_CURLY = 13;
	static final int OPEN_PAREN = 14;
	static final int OPTIONAL = 15;
	static final int OR = 16;
	static final int PLUS_CLOSE = 17;

	private static final int CLASS_CODE = 0;

	private static final int INIT_CODE = 1;

	private static final int EOF_CODE = 2;

	private static final int INIT_THROW_CODE = 3;

	private static final int YYLEX_THROW_CODE = 4;
	private static final int EOF_THROW_CODE = 5;
	private static final int EOF_VALUE_CODE = 6;
	/***************************************************************
	 Member Variables: JLex directives.
	 **************************************************************/
	private final char m_state_dir[] = { '%', 's', 't', 'a', 't', 'e', '\0' };
	private final char m_char_dir[] = { '%', 'c', 'h', 'a', 'r', '\0' };
	private final char m_line_dir[] = { '%', 'l', 'i', 'n', 'e', '\0' };
	private final char m_cup_dir[] = { '%', 'c', 'u', 'p', '\0' };

	private final char m_class_dir[] = { '%', 'c', 'l', 'a', 's', 's', '\0' };

	private final char m_implements_dir[] = { '%', 'i', 'm', 'p', 'l', 'e',
			'm', 'e', 'n', 't', 's', '\0' };

	private final char m_function_dir[] = { '%', 'f', 'u', 'n', 'c', 't', 'i',
			'o', 'n', '\0' };

	private final char m_type_dir[] = { '%', 't', 'y', 'p', 'e', '\0' };

	private final char m_integer_dir[] = { '%', 'i', 'n', 't', 'e', 'g', 'e',
			'r', '\0' };

	private final char m_intwrap_dir[] = { '%', 'i', 'n', 't', 'w', 'r', 'a',
			'p', '\0' };

	private final char m_full_dir[] = { '%', 'f', 'u', 'l', 'l', '\0' };

	private final char m_unicode_dir[] = { '%', 'u', 'n', 'i', 'c', 'o', 'd',
			'e', '\0' };

	private final char m_ignorecase_dir[] = { '%', 'i', 'g', 'n', 'o', 'r',
			'e', 'c', 'a', 's', 'e', '\0' };

	private final char m_notunix_dir[] = { '%', 'n', 'o', 't', 'u', 'n', 'i',
			'x', '\0' };

	private final char m_init_code_dir[] =
		{ '%', 'i', 'n', 'i', 't', '{', '\0' };

	private final char m_init_code_end_dir[] = { '%', 'i', 'n', 'i', 't', '}',
			'\0' };

	private final char m_init_throw_code_dir[] = { '%', 'i', 'n', 'i', 't',
			't', 'h', 'r', 'o', 'w', '{', '\0' };

	private final char m_init_throw_code_end_dir[] = { '%', 'i', 'n', 'i', 't',
			't', 'h', 'r', 'o', 'w', '}', '\0' };

	private final char m_yylex_throw_code_dir[] = { '%', 'y', 'y', 'l', 'e',
			'x', 't', 'h', 'r', 'o', 'w', '{', '\0' };

	private final char m_yylex_throw_code_end_dir[] = { '%', 'y', 'y', 'l',
			'e', 'x', 't', 'h', 'r', 'o', 'w', '}', '\0' };

	private final char m_eof_code_dir[] = { '%', 'e', 'o', 'f', '{', '\0' };

	private final char m_eof_code_end_dir[] = { '%', 'e', 'o', 'f', '}', '\0' };

	private final char m_eof_value_code_dir[] = { '%', 'e', 'o', 'f', 'v', 'a',
			'l', '{', '\0' };

	private final char m_eof_value_code_end_dir[] = { '%', 'e', 'o', 'f', 'v',
			'a', 'l', '}', '\0' };

	private final char m_eof_throw_code_dir[] = { '%', 'e', 'o', 'f', 't', 'h',
			'r', 'o', 'w', '{', '\0' };

	private final char m_eof_throw_code_end_dir[] = { '%', 'e', 'o', 'f', 't',
			'h', 'r', 'o', 'w', '}', '\0' };

	private final char m_class_code_dir[] = { '%', '{', '\0' };

	private final char m_class_code_end_dir[] = { '%', '}', '\0' };

	private final char m_yyeof_dir[] = { '%', 'y', 'y', 'e', 'o', 'f', '\0' };

	private final char m_public_dir[] = { '%', 'p', 'u', 'b', 'l', 'i', 'c',
			'\0' };

	/***************************************************************
	 Function: getStates
	 Description: Parses the state area of a rule,
	 from the beginning of a line.
	 < state1, state2 ... > regular_expression { action }
	 Returns null on only EOF.  Returns all_states, 
	 initialied properly to correspond to all states,
	 if no states are found.
	 Special Notes: This function treats commas as optional
	 and permits states to be spread over multiple lines.
	 **************************************************************/
	private SparseBitSet all_states = null;

	/********************************************************
	 Function: advance
	 Description: Returns code for next token.
	 *******************************************************/
	private boolean m_advance_stop = false;

	/***************************************************************
	 Function: CLexGen
	 **************************************************************/
	CLexGen(final String filename) throws java.io.FileNotFoundException,
			java.io.IOException {
		/* Successful initialization flag. */
		this.m_init_flag = false;

		/* Open input stream. */
		this.m_instream = new java.io.FileReader(filename);
		if (null == this.m_instream) {
			System.out.println("Error: Unable to open input file " + filename
					+ ".");
			return;
		}

		/* Open output stream. */
		this.m_outstream =
			new java.io.PrintWriter(new java.io.BufferedWriter(
				new java.io.FileWriter(filename + ".java")));
		if (null == this.m_outstream) {
			System.out.println("Error: Unable to open output file " + filename
					+ ".java.");
			return;
		}

		/* Create input buffer class. */
		this.m_input = new CInput(this.m_instream);

		/* Initialize character hash table. */
		this.m_tokens = new Hashtable();
		this.m_tokens.put(new Character('$'), new Integer(CLexGen.AT_EOL));
		this.m_tokens.put(new Character('('), new Integer(CLexGen.OPEN_PAREN));
		this.m_tokens.put(new Character(')'), new Integer(CLexGen.CLOSE_PAREN));
		this.m_tokens.put(new Character('*'), new Integer(CLexGen.CLOSURE));
		this.m_tokens.put(new Character('+'), new Integer(CLexGen.PLUS_CLOSE));
		this.m_tokens.put(new Character('-'), new Integer(CLexGen.DASH));
		this.m_tokens.put(new Character('.'), new Integer(CLexGen.ANY));
		this.m_tokens.put(new Character('?'), new Integer(CLexGen.OPTIONAL));
		this.m_tokens.put(new Character('['), new Integer(CLexGen.CCL_START));
		this.m_tokens.put(new Character(']'), new Integer(CLexGen.CCL_END));
		this.m_tokens.put(new Character('^'), new Integer(CLexGen.AT_BOL));
		this.m_tokens.put(new Character('{'), new Integer(CLexGen.OPEN_CURLY));
		this.m_tokens.put(new Character('|'), new Integer(CLexGen.OR));
		this.m_tokens.put(new Character('}'), new Integer(CLexGen.CLOSE_CURLY));

		/* Initialize spec structure. */
		this.m_spec = new CSpec(this);

		/* Nfa to dfa converter. */
		this.m_nfa2dfa = new CNfa2Dfa();
		this.m_minimize = new CMinimize();
		this.m_makeNfa = new CMakeNfa();
		this.m_simplifyNfa = new CSimplifyNfa();

		this.m_emit = new CEmit();

		/* Successful initialization flag. */
		this.m_init_flag = true;
	}

	int advance() throws java.io.IOException {
		boolean saw_escape = false;
		Integer code;

		/*if (m_input.m_line_index > m_input.m_line_read) {
		 System.out.println("m_input.m_line_index = " + m_input.m_line_index);
		 System.out.println("m_input.m_line_read = " + m_input.m_line_read);
		 CUtility.ASSERT(m_input.m_line_index <= m_input.m_line_read);
		 }*/

		if (this.m_input.m_eof_reached) {
			/* EOF has already been reached,
			 so return appropriate code. */

			this.m_spec.m_current_token = CLexGen.END_OF_INPUT;
			this.m_spec.m_lexeme = '\0';
			return this.m_spec.m_current_token;
		}

		/* End of previous regular expression?
		 Refill line buffer? */
		if (CLexGen.EOS == this.m_spec.m_current_token
		/* ADDED */
		|| this.m_input.m_line_index >= this.m_input.m_line_read)
		/* ADDED */
		{
			if (this.m_spec.m_in_quote) {
				CError.parse_error(CError.E_SYNTAX, this.m_input.m_line_number);
			}

			while (true) {
				if (false == this.m_advance_stop
						|| this.m_input.m_line_index >= this.m_input.m_line_read) {
					if (this.m_input.getLine()) {
						/* EOF has already been reached,
						 so return appropriate code. */

						this.m_spec.m_current_token = CLexGen.END_OF_INPUT;
						this.m_spec.m_lexeme = '\0';
						return this.m_spec.m_current_token;
					}
					this.m_input.m_line_index = 0;
				}
				else {
					this.m_advance_stop = false;
				}

				while (this.m_input.m_line_index < this.m_input.m_line_read
						&& true == CUtility
							.isspace(this.m_input.m_line[this.m_input.m_line_index])) {
					++this.m_input.m_line_index;
				}

				if (this.m_input.m_line_index < this.m_input.m_line_read) {
					break;
				}
			}
		}

		if (CUtility.DEBUG) {
			CUtility
				.ASSERT(this.m_input.m_line_index <= this.m_input.m_line_read);
		}

		while (true) {
			if (false == this.m_spec.m_in_quote
					&& '{' == this.m_input.m_line[this.m_input.m_line_index]) {
				if (false == this.expandMacro()) {
					break;
				}

				if (this.m_input.m_line_index >= this.m_input.m_line_read) {
					this.m_spec.m_current_token = CLexGen.EOS;
					this.m_spec.m_lexeme = '\0';
					return this.m_spec.m_current_token;
				}
			}
			else if ('\"' == this.m_input.m_line[this.m_input.m_line_index]) {
				this.m_spec.m_in_quote = !this.m_spec.m_in_quote;
				++this.m_input.m_line_index;

				if (this.m_input.m_line_index >= this.m_input.m_line_read) {
					this.m_spec.m_current_token = CLexGen.EOS;
					this.m_spec.m_lexeme = '\0';
					return this.m_spec.m_current_token;
				}
			}
			else {
				break;
			}
		}

		if (this.m_input.m_line_index > this.m_input.m_line_read) {
			System.out.println("m_input.m_line_index = "
					+ this.m_input.m_line_index);
			System.out.println("m_input.m_line_read = "
					+ this.m_input.m_line_read);
			CUtility
				.ASSERT(this.m_input.m_line_index <= this.m_input.m_line_read);
		}

		/* Look for backslash, and corresponding 
		 escape sequence. */
		if ('\\' == this.m_input.m_line[this.m_input.m_line_index]) {
			saw_escape = true;
		}
		else {
			saw_escape = false;
		}

		if (false == this.m_spec.m_in_quote) {
			if (false == this.m_spec.m_in_ccl
					&& CUtility
						.isspace(this.m_input.m_line[this.m_input.m_line_index])) {
				/* White space means the end of 
				 the current regular expression. */

				this.m_spec.m_current_token = CLexGen.EOS;
				this.m_spec.m_lexeme = '\0';
				return this.m_spec.m_current_token;
			}

			/* Process escape sequence, if needed. */
			if (saw_escape) {
				this.m_spec.m_lexeme = this.expandEscape();
			}
			else {
				this.m_spec.m_lexeme =
					this.m_input.m_line[this.m_input.m_line_index];
				++this.m_input.m_line_index;
			}
		}
		else {
			if (saw_escape
					&& this.m_input.m_line_index + 1 < this.m_input.m_line_read
					&& '\"' == this.m_input.m_line[this.m_input.m_line_index + 1]) {
				this.m_spec.m_lexeme = '\"';
				this.m_input.m_line_index = this.m_input.m_line_index + 2;
			}
			else {
				this.m_spec.m_lexeme =
					this.m_input.m_line[this.m_input.m_line_index];
				++this.m_input.m_line_index;
			}
		}

		code = (Integer) this.m_tokens.get(new Character(this.m_spec.m_lexeme));
		if (this.m_spec.m_in_quote || true == saw_escape) {
			this.m_spec.m_current_token = CLexGen.L;
		}
		else {
			if (null == code) {
				this.m_spec.m_current_token = CLexGen.L;
			}
			else {
				this.m_spec.m_current_token = code.intValue();
			}
		}

		if (CLexGen.CCL_START == this.m_spec.m_current_token) {
			this.m_spec.m_in_ccl = true;
		}
		if (CLexGen.CCL_END == this.m_spec.m_current_token) {
			this.m_spec.m_in_ccl = false;
		}

		if (CUtility.FOODEBUG) {
			System.out.println("Lexeme: " + this.m_spec.m_lexeme + "\tToken: "
					+ this.m_spec.m_current_token + "\tIndex: "
					+ this.m_input.m_line_index);
		}

		return this.m_spec.m_current_token;
	}

	/***************************************************************
	 Function: details
	 Description: High level debugging routine.
	 **************************************************************/
	private void details() {
		Enumeration names;
		String name;
		String def;
		Enumeration states;
		String state;
		Integer index;
		final int elem;
		final int size;

		System.out.println();
		System.out.println("\t** Macros **");
		names = this.m_spec.m_macros.keys();
		while (names.hasMoreElements()) {
			name = (String) names.nextElement();
			def = (String) this.m_spec.m_macros.get(name);

			if (CUtility.DEBUG) {
				CUtility.ASSERT(null != name);
				CUtility.ASSERT(null != def);
			}

			System.out.println("Macro name \"" + name + "\" has definition \""
					+ def + "\".");
		}

		System.out.println();
		System.out.println("\t** States **");
		states = this.m_spec.m_states.keys();
		while (states.hasMoreElements()) {
			state = (String) states.nextElement();
			index = (Integer) this.m_spec.m_states.get(state);

			if (CUtility.DEBUG) {
				CUtility.ASSERT(null != state);
				CUtility.ASSERT(null != index);
			}

			System.out.println("State \"" + state + "\" has identifying index "
					+ index.toString() + ".");
		}

		System.out.println();
		System.out.println("\t** Character Counting **");
		if (false == this.m_spec.m_count_chars) {
			System.out.println("Character counting is off.");
		}
		else {
			if (CUtility.DEBUG) {
				CUtility.ASSERT(this.m_spec.m_count_lines);
			}

			System.out.println("Character counting is on.");
		}

		System.out.println();
		System.out.println("\t** Line Counting **");
		if (false == this.m_spec.m_count_lines) {
			System.out.println("Line counting is off.");
		}
		else {
			if (CUtility.DEBUG) {
				CUtility.ASSERT(this.m_spec.m_count_lines);
			}

			System.out.println("Line counting is on.");
		}

		System.out.println();
		System.out.println("\t** Operating System Specificity **");
		if (false == this.m_spec.m_unix) {
			System.out.println("Not generating UNIX-specific code.");
			System.out.println("(This means that \"\\r\\n\" is a "
					+ "newline, rather than \"\\n\".)");
		}
		else {
			System.out.println("Generating UNIX-specific code.");
			System.out.println("(This means that \"\\n\" is a "
					+ "newline, rather than \"\\r\\n\".)");
		}

		System.out.println();
		System.out.println("\t** Java CUP Compatibility **");
		if (false == this.m_spec.m_cup_compatible) {
			System.out.println("Generating CUP compatible code.");
			System.out.println("(Scanner implements "
					+ "java_cup.runtime.Scanner.)");
		}
		else {
			System.out.println("Not generating CUP compatible code.");
		}

		if (CUtility.FOODEBUG) {
			if (null != this.m_spec.m_nfa_states
					&& null != this.m_spec.m_nfa_start) {
				System.out.println();
				System.out.println("\t** NFA machine **");
				this.print_nfa();
			}
		}

		if (null != this.m_spec.m_dtrans_vector) {
			System.out.println();
			System.out.println("\t** DFA transition table **");
			/*print_header();*/
		}

		/*if (null != m_spec.m_accept_vector && null != m_spec.m_anchor_array)
		 {
		 System.out.println();
		 System.out.println("\t** Accept States and Anchor Vector **");
		 print_accept();
		 }*/
	}

	/********************************************************
	 Function: expandEscape
	 Description: Takes escape sequence and returns
	 corresponding character code.
	 *******************************************************/
	private char expandEscape() {
		char r;

		/* Debug checks. */
		if (CUtility.DEBUG) {
			CUtility
				.ASSERT(this.m_input.m_line_index < this.m_input.m_line_read);
			CUtility.ASSERT(0 < this.m_input.m_line_read);
			CUtility.ASSERT(0 <= this.m_input.m_line_index);
		}

		if ('\\' != this.m_input.m_line[this.m_input.m_line_index]) {
			++this.m_input.m_line_index;
			return this.m_input.m_line[this.m_input.m_line_index - 1];
		}
		else {
			boolean unicode_escape = false;
			++this.m_input.m_line_index;
			switch (this.m_input.m_line[this.m_input.m_line_index]) {
				case 'b' :
					++this.m_input.m_line_index;
					return '\b';

				case 't' :
					++this.m_input.m_line_index;
					return '\t';

				case 'n' :
					++this.m_input.m_line_index;
					return '\n';

				case 'f' :
					++this.m_input.m_line_index;
					return '\f';

				case 'r' :
					++this.m_input.m_line_index;
					return '\r';

				case '^' :
					++this.m_input.m_line_index;
					r =
						Character
							.toUpperCase(this.m_input.m_line[this.m_input.m_line_index]);
					if (r < '@' || r > 'Z') {
						CError.parse_error(
							CError.E_BADCTRL,
							this.m_input.m_line_number);
					}
					r = (char) (r - '@');
					++this.m_input.m_line_index;
					return r;

				case 'u' :
					unicode_escape = true;
				case 'x' :
					++this.m_input.m_line_index;
					r = 0;
					for (int i = 0; i < (unicode_escape ? 4 : 2); i++) {
						if (CUtility
							.ishexdigit(this.m_input.m_line[this.m_input.m_line_index])) {
							r = (char) (r << 4);
							r =
								(char) (r | CUtility
									.hex2bin(this.m_input.m_line[this.m_input.m_line_index]));
							++this.m_input.m_line_index;
						}
						else {
							break;
						}
					}

					return r;

				default :
					if (false == CUtility
						.isoctdigit(this.m_input.m_line[this.m_input.m_line_index])) {
						r = this.m_input.m_line[this.m_input.m_line_index];
						++this.m_input.m_line_index;
					}
					else {
						r = 0;
						for (int i = 0; i < 3; i++) {
							if (CUtility
								.isoctdigit(this.m_input.m_line[this.m_input.m_line_index])) {
								r = (char) (r << 3);
								r =
									(char) (r | CUtility
										.oct2bin(this.m_input.m_line[this.m_input.m_line_index]));
								++this.m_input.m_line_index;
							}
							else {
								break;
							}
						}
					}
					return r;
			}
		}
	}

	/********************************************************
	 Function: expandMacro
	 Description: Returns false on error, true otherwise. 
	 *******************************************************/
	private boolean expandMacro() {
		int elem;
		int start_macro;
		int end_macro;
		int start_name;
		int count_name;
		String def;
		int def_elem;
		String name;
		char replace[];
		int rep_elem;

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
		}

		/* Check for macro. */
		if ('{' != this.m_input.m_line[this.m_input.m_line_index]) {
			CError.parse_error(CError.E_INTERNAL, this.m_input.m_line_number);
			return CLexGen.ERROR;
		}

		start_macro = this.m_input.m_line_index;
		elem = this.m_input.m_line_index + 1;
		if (elem >= this.m_input.m_line_read) {
			CError.impos("Unfinished macro name");
			return CLexGen.ERROR;
		}

		/* Get macro name. */
		start_name = elem;
		while ('}' != this.m_input.m_line[elem]) {
			++elem;
			if (elem >= this.m_input.m_line_read) {
				CError.impos("Unfinished macro name at line "
						+ this.m_input.m_line_number);
				return CLexGen.ERROR;
			}
		}
		count_name = elem - start_name;
		end_macro = elem;

		/* Check macro name. */
		if (0 == count_name) {
			CError.impos("Nonexistent macro name");
			return CLexGen.ERROR;
		}

		/* Debug checks. */
		if (CUtility.DEBUG) {
			CUtility.ASSERT(0 < count_name);
		}

		/* Retrieve macro definition. */
		name = new String(this.m_input.m_line, start_name, count_name);
		def = (String) this.m_spec.m_macros.get(name);
		if (null == def) {
			/*CError.impos("Undefined macro \"" + name + "\".");*/
			System.out.println("Error: Undefined macro \"" + name + "\".");
			CError.parse_error(CError.E_NOMAC, this.m_input.m_line_number);
			return CLexGen.ERROR;
		}
		if (CUtility.OLD_DUMP_DEBUG) {
			System.out.println("expanded escape: " + def);
		}

		/* Replace macro in new buffer,
		 beginning by copying first part of line buffer. */
		replace = new char[this.m_input.m_line.length];
		for (rep_elem = 0; rep_elem < start_macro; ++rep_elem) {
			replace[rep_elem] = this.m_input.m_line[rep_elem];

			if (CUtility.DEBUG) {
				CUtility.ASSERT(rep_elem < replace.length);
			}
		}

		/* Copy macro definition. */
		if (rep_elem >= replace.length) {
			replace = CUtility.doubleSize(replace);
		}
		for (def_elem = 0; def_elem < def.length(); ++def_elem) {
			replace[rep_elem] = def.charAt(def_elem);

			++rep_elem;
			if (rep_elem >= replace.length) {
				replace = CUtility.doubleSize(replace);
			}
		}

		/* Copy last part of line. */
		if (rep_elem >= replace.length) {
			replace = CUtility.doubleSize(replace);
		}
		for (elem = end_macro + 1; elem < this.m_input.m_line_read; ++elem) {
			replace[rep_elem] = this.m_input.m_line[elem];

			++rep_elem;
			if (rep_elem >= replace.length) {
				replace = CUtility.doubleSize(replace);
			}
		}

		/* Replace buffer. */
		this.m_input.m_line = replace;
		this.m_input.m_line_read = rep_elem;

		if (CUtility.OLD_DEBUG) {
			System.out.println(new String(
				this.m_input.m_line,
				0,
				this.m_input.m_line_read));
		}
		return CLexGen.NOT_ERROR;
	}

	/***************************************************************
	 Function: generate
	 Description: 
	 **************************************************************/
	void generate() throws java.io.IOException, java.io.FileNotFoundException {
		if (false == this.m_init_flag) {
			CError.parse_error(CError.E_INIT, 0);
		}

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
			CUtility.ASSERT(this.m_init_flag);
		}

		/*m_emit.emit_imports(m_spec,m_outstream);*/

		if (this.m_spec.m_verbose) {
			System.out.println("Processing first section -- user code.");
		}
		this.userCode();
		if (this.m_input.m_eof_reached) {
			CError.parse_error(CError.E_EOF, this.m_input.m_line_number);
		}

		if (this.m_spec.m_verbose) {
			System.out.println("Processing second section -- "
					+ "JLex declarations.");
		}
		this.userDeclare();
		if (this.m_input.m_eof_reached) {
			CError.parse_error(CError.E_EOF, this.m_input.m_line_number);
		}

		if (this.m_spec.m_verbose) {
			System.out.println("Processing third section -- lexical rules.");
		}
		this.userRules();
		if (CUtility.DO_DEBUG) {
			this.print_header();
		}

		if (this.m_spec.m_verbose) {
			System.out.println("Outputting lexical analyzer code.");
		}
		this.m_emit.emit(this.m_spec, this.m_outstream);

		if (this.m_spec.m_verbose && true == CUtility.OLD_DUMP_DEBUG) {
			this.details();
		}

		this.m_outstream.close();
	}

	/***************************************************************
	 Function: getName
	 **************************************************************/
	private char[] getName() {
		char buffer[];
		int elem;

		/* Skip white space. */
		while (this.m_input.m_line_index < this.m_input.m_line_read
				&& true == CUtility
					.isspace(this.m_input.m_line[this.m_input.m_line_index])) {
			++this.m_input.m_line_index;
		}

		/* No name? */
		if (this.m_input.m_line_index >= this.m_input.m_line_read) {
			CError.parse_error(CError.E_DIRECT, 0);
		}

		/* Determine length. */
		elem = this.m_input.m_line_index;
		while (elem < this.m_input.m_line_read
				&& false == CUtility.isnewline(this.m_input.m_line[elem])) {
			++elem;
		}

		/* Allocate non-terminated buffer of exact length. */
		buffer = new char[elem - this.m_input.m_line_index];

		/* Copy. */
		elem = 0;
		while (this.m_input.m_line_index < this.m_input.m_line_read
				&& false == CUtility
					.isnewline(this.m_input.m_line[this.m_input.m_line_index])) {
			buffer[elem] = this.m_input.m_line[this.m_input.m_line_index];
			++elem;
			++this.m_input.m_line_index;
		}

		return buffer;
	}

	SparseBitSet getStates() throws java.io.IOException {
		int start_state;
		int count_state;
		SparseBitSet states;
		String name;
		Integer index;
		int i;
		int size;

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
		}

		states = null;

		/* Skip white space. */
		while (CUtility.isspace(this.m_input.m_line[this.m_input.m_line_index])) {
			++this.m_input.m_line_index;

			while (this.m_input.m_line_index >= this.m_input.m_line_read) {
				/* Must just be an empty line. */
				if (this.m_input.getLine()) {
					/* EOF found. */
					return null;
				}
			}
		}

		/* Look for states. */
		if ('<' == this.m_input.m_line[this.m_input.m_line_index]) {
			++this.m_input.m_line_index;

			states = new SparseBitSet();

			/* Parse states. */
			while (true) {
				/* We may have reached the end of the line. */
				while (this.m_input.m_line_index >= this.m_input.m_line_read) {
					if (this.m_input.getLine()) {
						/* EOF found. */
						CError.parse_error(
							CError.E_EOF,
							this.m_input.m_line_number);
						return states;
					}
				}

				while (true) {
					/* Skip white space. */
					while (CUtility
						.isspace(this.m_input.m_line[this.m_input.m_line_index])) {
						++this.m_input.m_line_index;

						while (this.m_input.m_line_index >= this.m_input.m_line_read) {
							if (this.m_input.getLine()) {
								/* EOF found. */
								CError.parse_error(
									CError.E_EOF,
									this.m_input.m_line_number);
								return states;
							}
						}
					}

					if (',' != this.m_input.m_line[this.m_input.m_line_index]) {
						break;
					}

					++this.m_input.m_line_index;
				}

				if ('>' == this.m_input.m_line[this.m_input.m_line_index]) {
					++this.m_input.m_line_index;
					if (this.m_input.m_line_index < this.m_input.m_line_read) {
						this.m_advance_stop = true;
					}
					return states;
				}

				/* Read in state name. */
				start_state = this.m_input.m_line_index;
				while (false == CUtility
					.isspace(this.m_input.m_line[this.m_input.m_line_index])
						&& ',' != this.m_input.m_line[this.m_input.m_line_index]
						&& '>' != this.m_input.m_line[this.m_input.m_line_index]) {
					++this.m_input.m_line_index;

					if (this.m_input.m_line_index >= this.m_input.m_line_read) {
						/* End of line means end of state name. */
						break;
					}
				}
				count_state = this.m_input.m_line_index - start_state;

				/* Save name after checking definition. */
				name =
					new String(this.m_input.m_line, start_state, count_state);
				index = (Integer) this.m_spec.m_states.get(name);
				if (null == index) {
					/* Uninitialized state. */
					System.out.println("Uninitialized State Name: " + name);
					CError.parse_error(
						CError.E_STATE,
						this.m_input.m_line_number);
				}
				states.set(index.intValue());
			}
		}

		if (null == this.all_states) {
			this.all_states = new SparseBitSet();

			size = this.m_spec.m_states.size();
			for (i = 0; i < size; ++i) {
				this.all_states.set(i);
			}
		}

		if (this.m_input.m_line_index < this.m_input.m_line_read) {
			this.m_advance_stop = true;
		}
		return this.all_states;
	}

	/***************************************************************
	 Function: interp_int
	 Description:
	 **************************************************************/
	private String interp_int(final int i) {
		switch (i) {
			case '\b' :
				return new String("\\b");

			case '\t' :
				return new String("\\t");

			case '\n' :
				return new String("\\n");

			case '\f' :
				return new String("\\f");

			case '\r' :
				return new String("\\r");

			case ' ' :
				return new String("\\ ");

			default :
				return new Character((char) i).toString();
		}
	}

	/********************************************************
	 Function: packAccept
	 Description: Packages and returns CAccept 
	 for action next in input stream.
	 *******************************************************/
	CAccept packAccept() throws java.io.IOException {
		CAccept accept;
		char action[];
		int action_index;
		int brackets;
		boolean insinglequotes;
		boolean indoublequotes;
		boolean instarcomment;
		boolean inslashcomment;
		boolean escaped;
		boolean slashed;

		action = new char[CLexGen.BUFFER_SIZE];
		action_index = 0;

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
		}

		/* Get a new line, if needed. */
		while (this.m_input.m_line_index >= this.m_input.m_line_read) {
			if (this.m_input.getLine()) {
				CError.parse_error(CError.E_EOF, this.m_input.m_line_number);
				return null;
			}
		}

		/* Look for beginning of action. */
		while (CUtility.isspace(this.m_input.m_line[this.m_input.m_line_index])) {
			++this.m_input.m_line_index;

			/* Get a new line, if needed. */
			while (this.m_input.m_line_index >= this.m_input.m_line_read) {
				if (this.m_input.getLine()) {
					CError
						.parse_error(CError.E_EOF, this.m_input.m_line_number);
					return null;
				}
			}
		}

		/* Look for brackets. */
		if ('{' != this.m_input.m_line[this.m_input.m_line_index]) {
			CError.parse_error(CError.E_BRACE, this.m_input.m_line_number);
		}

		/* Copy new line into action buffer. */
		brackets = 0;
		insinglequotes =
			indoublequotes =
				inslashcomment = instarcomment = escaped = slashed = false;
		while (true) {
			action[action_index] =
				this.m_input.m_line[this.m_input.m_line_index];

			/* Look for quotes. */
			if ((insinglequotes || indoublequotes) && escaped) {
				escaped = false; // only protects one char, but this is enough.
			}
			else if ((insinglequotes || indoublequotes)
					&& '\\' == this.m_input.m_line[this.m_input.m_line_index]) {
				escaped = true;
			}
			else if (!(insinglequotes || inslashcomment || instarcomment)
					&& '\"' == this.m_input.m_line[this.m_input.m_line_index]) {
				indoublequotes = !indoublequotes; // unescaped double quote.
			}
			else if (!(indoublequotes || inslashcomment || instarcomment)
					&& '\'' == this.m_input.m_line[this.m_input.m_line_index]) {
				insinglequotes = !insinglequotes; // unescaped single quote.
			}
			/* Look for comments. */
			if (instarcomment) { // inside "/*" comment; look for "*/"
				if (slashed
						&& '/' == this.m_input.m_line[this.m_input.m_line_index]) {
					instarcomment = slashed = false;
				}
				else {
					// note that inside a star comment, slashed means starred
					slashed =
						'*' == this.m_input.m_line[this.m_input.m_line_index];
				}
			}
			else if (!inslashcomment && !insinglequotes && !indoublequotes) {
				// not in comment, look for /* or //
				inslashcomment =
					slashed
							&& '/' == this.m_input.m_line[this.m_input.m_line_index];
				instarcomment =
					slashed
							&& '*' == this.m_input.m_line[this.m_input.m_line_index];
				slashed = '/' == this.m_input.m_line[this.m_input.m_line_index];
			}

			/* Look for brackets. */
			if (!insinglequotes && !indoublequotes && !instarcomment
					&& !inslashcomment) {
				if ('{' == this.m_input.m_line[this.m_input.m_line_index]) {
					++brackets;
				}
				else if ('}' == this.m_input.m_line[this.m_input.m_line_index]) {
					--brackets;

					if (0 == brackets) {
						++action_index;
						++this.m_input.m_line_index;

						break;
					}
				}
			}

			++action_index;
			/* Double the buffer size, if needed. */
			if (action_index >= action.length) {
				action = CUtility.doubleSize(action);
			}

			++this.m_input.m_line_index;
			/* Get a new line, if needed. */
			while (this.m_input.m_line_index >= this.m_input.m_line_read) {
				inslashcomment = slashed = false;
				if (insinglequotes || indoublequotes) { // non-fatal
					CError.parse_error(
						CError.E_NEWLINE,
						this.m_input.m_line_number);
					insinglequotes = indoublequotes = false;
				}
				if (this.m_input.getLine()) {
					CError.parse_error(
						CError.E_SYNTAX,
						this.m_input.m_line_number);
					return null;
				}
			}
		}

		accept = new CAccept(action, action_index, this.m_input.m_line_number);

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != accept);
		}

		if (CUtility.DESCENT_DEBUG) {
			System.out.print("Accepting action:");
			System.out.println(new String(
				accept.m_action,
				0,
				accept.m_action_read));
		}

		return accept;
	}
	/***************************************************************
	 Function: packCode
	 Description:
	 **************************************************************/
	private char[] packCode(
		final char start_dir[],
		final char end_dir[],
		char prev_code[],
		int prev_read,
		final int specified) throws java.io.IOException {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(CLexGen.INIT_CODE == specified
					|| CLexGen.CLASS_CODE == specified
					|| CLexGen.EOF_CODE == specified
					|| CLexGen.EOF_VALUE_CODE == specified
					|| CLexGen.INIT_THROW_CODE == specified
					|| CLexGen.YYLEX_THROW_CODE == specified
					|| CLexGen.EOF_THROW_CODE == specified);
		}

		if (0 != CUtility.charncmp(
			this.m_input.m_line,
			0,
			start_dir,
			0,
			start_dir.length - 1)) {
			CError.parse_error(CError.E_INTERNAL, 0);
		}

		if (null == prev_code) {
			prev_code = new char[CLexGen.BUFFER_SIZE];
			prev_read = 0;
		}

		if (prev_read >= prev_code.length) {
			prev_code = CUtility.doubleSize(prev_code);
		}

		this.m_input.m_line_index = start_dir.length - 1;
		while (true) {
			while (this.m_input.m_line_index >= this.m_input.m_line_read) {
				if (this.m_input.getLine()) {
					CError
						.parse_error(CError.E_EOF, this.m_input.m_line_number);
				}

				if (0 == CUtility.charncmp(
					this.m_input.m_line,
					0,
					end_dir,
					0,
					end_dir.length - 1)) {
					this.m_input.m_line_index = end_dir.length - 1;

					switch (specified) {
						case CLASS_CODE :
							this.m_spec.m_class_read = prev_read;
							break;

						case INIT_CODE :
							this.m_spec.m_init_read = prev_read;
							break;

						case EOF_CODE :
							this.m_spec.m_eof_read = prev_read;
							break;

						case EOF_VALUE_CODE :
							this.m_spec.m_eof_value_read = prev_read;
							break;

						case INIT_THROW_CODE :
							this.m_spec.m_init_throw_read = prev_read;
							break;

						case YYLEX_THROW_CODE :
							this.m_spec.m_yylex_throw_read = prev_read;
							break;

						case EOF_THROW_CODE :
							this.m_spec.m_eof_throw_read = prev_read;
							break;

						default :
							CError.parse_error(
								CError.E_INTERNAL,
								this.m_input.m_line_number);
							break;
					}

					return prev_code;
				}
			}

			while (this.m_input.m_line_index < this.m_input.m_line_read) {
				prev_code[prev_read] =
					this.m_input.m_line[this.m_input.m_line_index];
				++prev_read;
				++this.m_input.m_line_index;

				if (prev_read >= prev_code.length) {
					prev_code = CUtility.doubleSize(prev_code);
				}
			}
		}
	}

	/***************************************************************
	 Function: plab
	 Description:
	 **************************************************************/
	private String plab(final CNfa state) {
		int index;

		if (null == state) {
			return new String("--");
		}

		index = this.m_spec.m_nfa_states.indexOf(state);

		return new Integer(index).toString();
	}

	/***************************************************************
	 Function: print_header
	 **************************************************************/
	private void print_header() {
		Enumeration states;
		int i;
		int j;
		int chars_printed = 0;
		CDTrans dtrans;
		int last_transition;
		String str;
		CAccept accept;
		String state;
		Integer index;

		System.out
			.println("/*---------------------- DFA -----------------------");

		states = this.m_spec.m_states.keys();
		while (states.hasMoreElements()) {
			state = (String) states.nextElement();
			index = (Integer) this.m_spec.m_states.get(state);

			if (CUtility.DEBUG) {
				CUtility.ASSERT(null != state);
				CUtility.ASSERT(null != index);
			}

			System.out.println("State \"" + state + "\" has identifying index "
					+ index.toString() + ".");

			i = index.intValue();
			if (CDTrans.F != this.m_spec.m_state_dtrans[i]) {
				System.out.println("\tStart index in transition table: "
						+ this.m_spec.m_state_dtrans[i]);
			}
			else {
				System.out.println("\tNo associated transition states.");
			}
		}

		for (i = 0; i < this.m_spec.m_dtrans_vector.size(); ++i) {
			dtrans = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(i);

			if (null == this.m_spec.m_accept_vector
					&& null == this.m_spec.m_anchor_array) {
				if (null == dtrans.m_accept) {
					System.out.print(" * State " + i + " [nonaccepting]");
				}
				else {
					System.out.print(" * State "
							+ i
							+ " [accepting, line "
							+ dtrans.m_accept.m_line_number
							+ " <"
							+ new String(
								dtrans.m_accept.m_action,
								0,
								dtrans.m_accept.m_action_read) + ">]");
					if (CSpec.NONE != dtrans.m_anchor) {
						System.out
							.print(" Anchor: "
									+ (0 != (dtrans.m_anchor & CSpec.START) ? "start "
											: "")
									+ (0 != (dtrans.m_anchor & CSpec.END) ? "end "
											: ""));
					}
				}
			}
			else {
				accept = (CAccept) this.m_spec.m_accept_vector.elementAt(i);

				if (null == accept) {
					System.out.print(" * State " + i + " [nonaccepting]");
				}
				else {
					System.out.print(" * State "
							+ i
							+ " [accepting, line "
							+ accept.m_line_number
							+ " <"
							+ new String(
								accept.m_action,
								0,
								accept.m_action_read) + ">]");
					if (CSpec.NONE != this.m_spec.m_anchor_array[i]) {
						System.out
							.print(" Anchor: "
									+ (0 != (this.m_spec.m_anchor_array[i] & CSpec.START) ? "start "
											: "")
									+ (0 != (this.m_spec.m_anchor_array[i] & CSpec.END) ? "end "
											: ""));
					}
				}
			}

			last_transition = -1;
			for (j = 0; j < this.m_spec.m_dtrans_ncols; ++j) {
				if (CDTrans.F != dtrans.m_dtrans[j]) {
					if (last_transition != dtrans.m_dtrans[j]) {
						System.out.println();
						System.out.print(" *    goto " + dtrans.m_dtrans[j]
								+ " on ");
						chars_printed = 0;
					}

					str = this.interp_int(j);
					System.out.print(str);

					chars_printed = chars_printed + str.length();
					if (56 < chars_printed) {
						System.out.println();
						System.out.print(" *             ");
						chars_printed = 0;
					}

					last_transition = dtrans.m_dtrans[j];
				}
			}
			System.out.println();
		}
		System.out.println(" */");
		System.out.println();
	}

	/***************************************************************
	 Function: print_nfa
	 Description:
	 **************************************************************/
	void print_nfa() {
		int elem;
		CNfa nfa;
		int size;
		Enumeration states;
		Integer index;
		int i;
		int j;
		int vsize;
		String state;

		System.out.println("--------------------- NFA -----------------------");

		size = this.m_spec.m_nfa_states.size();
		for (elem = 0; elem < size; ++elem) {
			nfa = (CNfa) this.m_spec.m_nfa_states.elementAt(elem);

			System.out.print("Nfa state " + this.plab(nfa) + ": ");

			if (null == nfa.m_next) {
				System.out.print("(TERMINAL)");
			}
			else {
				System.out.print("--> " + this.plab(nfa.m_next));
				System.out.print("--> " + this.plab(nfa.m_next2));

				switch (nfa.m_edge) {
					case CNfa.CCL :
						this.printccl(nfa.m_set);
						break;

					case CNfa.EPSILON :
						System.out.print(" EPSILON ");
						break;

					default :
						System.out.print(" " + this.interp_int(nfa.m_edge));
						break;
				}
			}

			if (0 == elem) {
				System.out.print(" (START STATE)");
			}

			if (null != nfa.m_accept) {
				System.out.print(" accepting "
						+ (0 != (nfa.m_anchor & CSpec.START) ? "^" : "")
						+ "<"
						+ new String(
							nfa.m_accept.m_action,
							0,
							nfa.m_accept.m_action_read) + ">"
						+ (0 != (nfa.m_anchor & CSpec.END) ? "$" : ""));
			}

			System.out.println();
		}

		states = this.m_spec.m_states.keys();
		while (states.hasMoreElements()) {
			state = (String) states.nextElement();
			index = (Integer) this.m_spec.m_states.get(state);

			if (CUtility.DEBUG) {
				CUtility.ASSERT(null != state);
				CUtility.ASSERT(null != index);
			}

			System.out.println("State \"" + state + "\" has identifying index "
					+ index.toString() + ".");
			System.out.print("\tStart states of matching rules: ");

			i = index.intValue();
			vsize = this.m_spec.m_state_rules[i].size();

			for (j = 0; j < vsize; ++j) {
				nfa = (CNfa) this.m_spec.m_state_rules[i].elementAt(j);

				System.out.print(this.m_spec.m_nfa_states.indexOf(nfa) + " ");
			}

			System.out.println();
		}

		System.out.println("-------------------- NFA ----------------------");
	}

	/***************************************************************
	 function: print_set
	 **************************************************************/
	void print_set(final Vector nfa_set) {
		int size;
		int elem;
		CNfa nfa;

		size = nfa_set.size();

		if (0 == size) {
			System.out.print("empty ");
		}

		for (elem = 0; elem < size; ++elem) {
			nfa = (CNfa) nfa_set.elementAt(elem);
			/*System.out.print(m_spec.m_nfa_states.indexOf(nfa) + " ");*/
			System.out.print(nfa.m_label + " ");
		}
	}

	/***************************************************************
	 Function: printccl
	 Description: Debugging routine that outputs readable form
	 of character class.
	 **************************************************************/
	private void printccl(final CSet set) {
		int i;

		System.out.print(" [");
		for (i = 0; i < this.m_spec.m_dtrans_ncols; ++i) {
			if (set.contains(i)) {
				System.out.print(this.interp_int(i));
			}
		}
		System.out.print(']');
	}

	/***************************************************************
	 Function: saveMacro
	 Description: Saves macro definition of form:
	 macro_name = macro_definition
	 **************************************************************/
	private void saveMacro() {
		int elem;
		int start_name;
		int count_name;
		int start_def;
		int count_def;
		boolean saw_escape;
		boolean in_quote;
		boolean in_ccl;

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
		}

		/* Macro declarations are of the following form:
		 macro_name macro_definition */

		elem = 0;

		/* Skip white space preceding macro name. */
		while (CUtility.isspace(this.m_input.m_line[elem])) {
			++elem;
			if (elem >= this.m_input.m_line_read) {
				/* End of line has been reached,
				 and line was found to be empty. */
				return;
			}
		}

		/* Read macro name. */
		start_name = elem;
		while (false == CUtility.isspace(this.m_input.m_line[elem])
				&& '=' != this.m_input.m_line[elem]) {
			++elem;
			if (elem >= this.m_input.m_line_read) {
				/* Macro name but no associated definition. */
				CError.parse_error(CError.E_MACDEF, this.m_input.m_line_number);
			}
		}
		count_name = elem - start_name;

		/* Check macro name. */
		if (0 == count_name) {
			/* Nonexistent macro name. */
			CError.parse_error(CError.E_MACDEF, this.m_input.m_line_number);
		}

		/* Skip white space between name and definition. */
		while (CUtility.isspace(this.m_input.m_line[elem])) {
			++elem;
			if (elem >= this.m_input.m_line_read) {
				/* Macro name but no associated definition. */
				CError.parse_error(CError.E_MACDEF, this.m_input.m_line_number);
			}
		}

		if ('=' == this.m_input.m_line[elem]) {
			++elem;
			if (elem >= this.m_input.m_line_read) {
				/* Macro name but no associated definition. */
				CError.parse_error(CError.E_MACDEF, this.m_input.m_line_number);
			}
		}
		else {
			/* macro definition without = */
			CError.parse_error(CError.E_MACDEF, this.m_input.m_line_number);
		}

		/* Skip white space between name and definition. */
		while (CUtility.isspace(this.m_input.m_line[elem])) {
			++elem;
			if (elem >= this.m_input.m_line_read) {
				/* Macro name but no associated definition. */
				CError.parse_error(CError.E_MACDEF, this.m_input.m_line_number);
			}
		}

		/* Read macro definition. */
		start_def = elem;
		in_quote = false;
		in_ccl = false;
		saw_escape = false;
		while (false == CUtility.isspace(this.m_input.m_line[elem])
				|| true == in_quote || true == in_ccl || true == saw_escape) {
			if ('\"' == this.m_input.m_line[elem] && false == saw_escape) {
				in_quote = !in_quote;
			}

			if ('\\' == this.m_input.m_line[elem] && false == saw_escape) {
				saw_escape = true;
			}
			else {
				saw_escape = false;
			}
			if (false == saw_escape && false == in_quote) { // CSA, 24-jul-99
				if ('[' == this.m_input.m_line[elem] && false == in_ccl) {
					in_ccl = true;
				}
				if (']' == this.m_input.m_line[elem] && true == in_ccl) {
					in_ccl = false;
				}
			}

			++elem;
			if (elem >= this.m_input.m_line_read) {
				/* End of line. */
				break;
			}
		}
		count_def = elem - start_def;

		/* Check macro definition. */
		if (0 == count_def) {
			/* Nonexistent macro name. */
			CError.parse_error(CError.E_MACDEF, this.m_input.m_line_number);
		}

		/* Debug checks. */
		if (CUtility.DEBUG) {
			CUtility.ASSERT(0 < count_def);
			CUtility.ASSERT(0 < count_name);
			CUtility.ASSERT(null != this.m_spec.m_macros);
		}

		if (CUtility.OLD_DEBUG) {
			System.out.println("macro name \""
					+ new String(this.m_input.m_line, start_name, count_name)
					+ "\".");
			System.out.println("macro definition \""
					+ new String(this.m_input.m_line, start_def, count_def)
					+ "\".");
		}

		/* Add macro name and definition to table. */
		this.m_spec.m_macros.put(new String(
			this.m_input.m_line,
			start_name,
			count_name), new String(this.m_input.m_line, start_def, count_def));
	}
	/***************************************************************
	 Function: saveStates
	 Description: Takes state declaration and makes entries
	 for them in state hashtable in CSpec structure.
	 State declaration should be of the form:
	 %state name0[, name1, name2 ...]
	 (But commas are actually optional as long as there is 
	 white space in between them.)
	 **************************************************************/
	private void saveStates() {
		int start_state;
		int count_state;

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
		}

		/* EOF found? */
		if (this.m_input.m_eof_reached) {
			return;
		}

		/* Debug checks. */
		if (CUtility.DEBUG) {
			CUtility.ASSERT('%' == this.m_input.m_line[0]);
			CUtility.ASSERT('s' == this.m_input.m_line[1]);
			CUtility
				.ASSERT(this.m_input.m_line_index <= this.m_input.m_line_read);
			CUtility.ASSERT(0 <= this.m_input.m_line_index);
			CUtility.ASSERT(0 <= this.m_input.m_line_read);
		}

		/* Blank line?  No states? */
		if (this.m_input.m_line_index >= this.m_input.m_line_read) {
			return;
		}

		while (this.m_input.m_line_index < this.m_input.m_line_read) {
			if (CUtility.OLD_DEBUG) {
				System.out.println("line read " + this.m_input.m_line_read
						+ "\tline index = " + this.m_input.m_line_index);
			}

			/* Skip white space. */
			while (CUtility
				.isspace(this.m_input.m_line[this.m_input.m_line_index])) {
				++this.m_input.m_line_index;
				if (this.m_input.m_line_index >= this.m_input.m_line_read) {
					/* No more states to be found. */
					return;
				}
			}

			/* Look for state name. */
			start_state = this.m_input.m_line_index;
			while (false == CUtility
				.isspace(this.m_input.m_line[this.m_input.m_line_index])
					&& ',' != this.m_input.m_line[this.m_input.m_line_index]) {
				++this.m_input.m_line_index;
				if (this.m_input.m_line_index >= this.m_input.m_line_read) {
					/* End of line and end of state name. */
					break;
				}
			}
			count_state = this.m_input.m_line_index - start_state;

			if (CUtility.OLD_DEBUG) {
				System.out.println("State name \""
						+ new String(
							this.m_input.m_line,
							start_state,
							count_state) + "\".");
				System.out.println("Integer index \""
						+ this.m_spec.m_states.size() + "\".");
			}

			/* Enter new state name, along with unique index. */
			this.m_spec.m_states.put(new String(
				this.m_input.m_line,
				start_state,
				count_state), new Integer(this.m_spec.m_states.size()));

			/* Skip comma. */
			if (',' == this.m_input.m_line[this.m_input.m_line_index]) {
				++this.m_input.m_line_index;
				if (this.m_input.m_line_index >= this.m_input.m_line_read) {
					/* End of line. */
					return;
				}
			}
		}
	}

	/***************************************************************
	 Function: userCode
	 Description: Process first section of specification,
	 echoing it into output file.
	 **************************************************************/
	private void userCode() throws java.io.IOException {
		final int count = 0;

		if (false == this.m_init_flag) {
			CError.parse_error(CError.E_INIT, 0);
		}

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
		}

		if (this.m_input.m_eof_reached) {
			CError.parse_error(CError.E_EOF, 0);
		}

		while (true) {
			if (this.m_input.getLine()) {
				/* Eof reached. */
				CError.parse_error(CError.E_EOF, 0);
			}

			if (2 <= this.m_input.m_line_read && '%' == this.m_input.m_line[0]
					&& '%' == this.m_input.m_line[1]) {
				/* Discard remainder of line. */
				this.m_input.m_line_index = this.m_input.m_line_read;
				return;
			}

			this.m_outstream.print(new String(
				this.m_input.m_line,
				0,
				this.m_input.m_line_read));
		}
	}

	/***************************************************************
	 Function: userDeclare
	 Description:
	 **************************************************************/
	private void userDeclare() throws java.io.IOException {
		final int elem;

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
		}

		if (this.m_input.m_eof_reached) {
			/* End-of-file. */
			CError.parse_error(CError.E_EOF, this.m_input.m_line_number);
		}

		while (false == this.m_input.getLine()) {
			/* Look for double percent. */
			if (2 <= this.m_input.m_line_read && '%' == this.m_input.m_line[0]
					&& '%' == this.m_input.m_line[1]) {
				/* Mess around with line. */
				this.m_input.m_line_read -= 2;
				System.arraycopy(
					this.m_input.m_line,
					2,
					this.m_input.m_line,
					0,
					this.m_input.m_line_read);

				this.m_input.m_pushback_line = true;
				/* Check for and discard empty line. */
				if (0 == this.m_input.m_line_read
						|| '\n' == this.m_input.m_line[0]) {
					this.m_input.m_pushback_line = false;
				}

				return;
			}

			if (0 == this.m_input.m_line_read) {
				continue;
			}

			if ('%' == this.m_input.m_line[0]) {
				/* Special lex declarations. */
				if (1 >= this.m_input.m_line_read) {
					CError.parse_error(
						CError.E_DIRECT,
						this.m_input.m_line_number);
					continue;
				}

				switch (this.m_input.m_line[1]) {
					case '{' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_class_code_dir,
							0,
							this.m_class_code_dir.length - 1)) {
							this.m_spec.m_class_code =
								this.packCode(
									this.m_class_code_dir,
									this.m_class_code_end_dir,
									this.m_spec.m_class_code,
									this.m_spec.m_class_read,
									CLexGen.CLASS_CODE);
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'c' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_char_dir,
							0,
							this.m_char_dir.length - 1)) {
							/* Set line counting to ON. */
							this.m_input.m_line_index = this.m_char_dir.length;
							this.m_spec.m_count_chars = true;
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_class_dir,
							0,
							this.m_class_dir.length - 1)) {
							this.m_input.m_line_index = this.m_class_dir.length;
							this.m_spec.m_class_name = this.getName();
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_cup_dir,
							0,
							this.m_cup_dir.length - 1)) {
							/* Set Java CUP compatibility to ON. */
							this.m_input.m_line_index = this.m_cup_dir.length;
							this.m_spec.m_cup_compatible = true;
							// this is what %cup does: [CSA, 27-Jul-1999]
							this.m_spec.m_implements_name =
								"java_cup.runtime.Scanner".toCharArray();
							this.m_spec.m_function_name =
								"next_token".toCharArray();
							this.m_spec.m_type_name =
								"java_cup.runtime.Symbol".toCharArray();
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'e' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_eof_code_dir,
							0,
							this.m_eof_code_dir.length - 1)) {
							this.m_spec.m_eof_code =
								this.packCode(
									this.m_eof_code_dir,
									this.m_eof_code_end_dir,
									this.m_spec.m_eof_code,
									this.m_spec.m_eof_read,
									CLexGen.EOF_CODE);
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_eof_value_code_dir,
							0,
							this.m_eof_value_code_dir.length - 1)) {
							this.m_spec.m_eof_value_code =
								this.packCode(
									this.m_eof_value_code_dir,
									this.m_eof_value_code_end_dir,
									this.m_spec.m_eof_value_code,
									this.m_spec.m_eof_value_read,
									CLexGen.EOF_VALUE_CODE);
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_eof_throw_code_dir,
							0,
							this.m_eof_throw_code_dir.length - 1)) {
							this.m_spec.m_eof_throw_code =
								this.packCode(
									this.m_eof_throw_code_dir,
									this.m_eof_throw_code_end_dir,
									this.m_spec.m_eof_throw_code,
									this.m_spec.m_eof_throw_read,
									CLexGen.EOF_THROW_CODE);
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'f' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_function_dir,
							0,
							this.m_function_dir.length - 1)) {
							/* Set line counting to ON. */
							this.m_input.m_line_index =
								this.m_function_dir.length;
							this.m_spec.m_function_name = this.getName();
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_full_dir,
							0,
							this.m_full_dir.length - 1)) {
							this.m_input.m_line_index = this.m_full_dir.length;
							this.m_spec.m_dtrans_ncols =
								CUtility.MAX_EIGHT_BIT + 1;
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'i' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_integer_dir,
							0,
							this.m_integer_dir.length - 1)) {
							/* Set line counting to ON. */
							this.m_input.m_line_index =
								this.m_integer_dir.length;
							this.m_spec.m_integer_type = true;
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_intwrap_dir,
							0,
							this.m_intwrap_dir.length - 1)) {
							/* Set line counting to ON. */
							this.m_input.m_line_index =
								this.m_integer_dir.length;
							this.m_spec.m_intwrap_type = true;
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_init_code_dir,
							0,
							this.m_init_code_dir.length - 1)) {
							this.m_spec.m_init_code =
								this.packCode(
									this.m_init_code_dir,
									this.m_init_code_end_dir,
									this.m_spec.m_init_code,
									this.m_spec.m_init_read,
									CLexGen.INIT_CODE);
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_init_throw_code_dir,
							0,
							this.m_init_throw_code_dir.length - 1)) {
							this.m_spec.m_init_throw_code =
								this.packCode(
									this.m_init_throw_code_dir,
									this.m_init_throw_code_end_dir,
									this.m_spec.m_init_throw_code,
									this.m_spec.m_init_throw_read,
									CLexGen.INIT_THROW_CODE);
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_implements_dir,
							0,
							this.m_implements_dir.length - 1)) {
							this.m_input.m_line_index =
								this.m_implements_dir.length;
							this.m_spec.m_implements_name = this.getName();
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_ignorecase_dir,
							0,
							this.m_ignorecase_dir.length - 1)) {
							/* Set m_ignorecase to ON. */
							this.m_input.m_line_index =
								this.m_ignorecase_dir.length;
							this.m_spec.m_ignorecase = true;
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'l' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_line_dir,
							0,
							this.m_line_dir.length - 1)) {
							/* Set line counting to ON. */
							this.m_input.m_line_index = this.m_line_dir.length;
							this.m_spec.m_count_lines = true;
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'n' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_notunix_dir,
							0,
							this.m_notunix_dir.length - 1)) {
							/* Set line counting to ON. */
							this.m_input.m_line_index =
								this.m_notunix_dir.length;
							this.m_spec.m_unix = false;
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'p' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_public_dir,
							0,
							this.m_public_dir.length - 1)) {
							/* Set public flag. */
							this.m_input.m_line_index =
								this.m_public_dir.length;
							this.m_spec.m_public = true;
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 's' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_state_dir,
							0,
							this.m_state_dir.length - 1)) {
							/* Recognize state list. */
							this.m_input.m_line_index = this.m_state_dir.length;
							this.saveStates();
							break;
						}

						/* Undefined directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 't' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_type_dir,
							0,
							this.m_type_dir.length - 1)) {
							/* Set Java CUP compatibility to ON. */
							this.m_input.m_line_index = this.m_type_dir.length;
							this.m_spec.m_type_name = this.getName();
							break;
						}

						/* Undefined directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'u' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_unicode_dir,
							0,
							this.m_unicode_dir.length - 1)) {
							this.m_input.m_line_index =
								this.m_unicode_dir.length;
							this.m_spec.m_dtrans_ncols =
								CUtility.MAX_SIXTEEN_BIT + 1;
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					case 'y' :
						if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_yyeof_dir,
							0,
							this.m_yyeof_dir.length - 1)) {
							this.m_input.m_line_index = this.m_yyeof_dir.length;
							this.m_spec.m_yyeof = true;
							break;
						}
						else if (0 == CUtility.charncmp(
							this.m_input.m_line,
							0,
							this.m_yylex_throw_code_dir,
							0,
							this.m_yylex_throw_code_dir.length - 1)) {
							this.m_spec.m_yylex_throw_code =
								this.packCode(
									this.m_yylex_throw_code_dir,
									this.m_yylex_throw_code_end_dir,
									this.m_spec.m_yylex_throw_code,
									this.m_spec.m_yylex_throw_read,
									CLexGen.YYLEX_THROW_CODE);
							break;
						}

						/* Bad directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;

					default :
						/* Undefined directive. */
						CError.parse_error(
							CError.E_DIRECT,
							this.m_input.m_line_number);
						break;
				}
			}
			else {
				/* Regular expression macro. */
				this.m_input.m_line_index = 0;
				this.saveMacro();
			}

			if (CUtility.OLD_DEBUG) {
				System.out.println("Line number " + this.m_input.m_line_number
						+ ":");
				System.out.print(new String(
					this.m_input.m_line,
					0,
					this.m_input.m_line_read));
			}
		}
	}

	/***************************************************************
	 Function: userRules
	 Description: Processes third section of JLex 
	 specification and creates minimized transition table.
	 **************************************************************/
	private void userRules() throws java.io.IOException {
		final int code;

		if (false == this.m_init_flag) {
			CError.parse_error(CError.E_INIT, 0);
		}

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != this);
			CUtility.ASSERT(null != this.m_outstream);
			CUtility.ASSERT(null != this.m_input);
			CUtility.ASSERT(null != this.m_tokens);
			CUtility.ASSERT(null != this.m_spec);
		}

		/* UNDONE: Need to handle states preceding rules. */

		if (this.m_spec.m_verbose) {
			System.out.println("Creating NFA machine representation.");
		}
		this.m_makeNfa.allocate_BOL_EOF(this.m_spec);
		this.m_makeNfa.thompson(this, this.m_spec, this.m_input);

		this.m_simplifyNfa.simplify(this.m_spec);

		/*print_nfa();*/

		if (CUtility.DEBUG) {
			CUtility
				.ASSERT(CLexGen.END_OF_INPUT == this.m_spec.m_current_token);
		}

		if (this.m_spec.m_verbose) {
			System.out.println("Creating DFA transition table.");
		}
		this.m_nfa2dfa.make_dfa(this, this.m_spec);

		if (CUtility.FOODEBUG) {
			this.print_header();
		}

		if (this.m_spec.m_verbose) {
			System.out.println("Minimizing DFA transition table.");
		}
		this.m_minimize.min_dfa(this.m_spec);
	}
}

/***************************************************************
 Class: CMakeNfa
 **************************************************************/
class CMakeNfa {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	private CSpec m_spec;
	private CLexGen m_lexGen;
	private CInput m_input;

	/***************************************************************
	 Function: CMakeNfa
	 Description: Constructor.
	 **************************************************************/
	CMakeNfa() {
		this.reset();
	}

	/***************************************************************
	 Function: allocate_BOL_EOF
	 Description: Expands character class to include special BOL and
	 EOF characters.  Puts numeric index of these characters in
	 input CSpec.
	 **************************************************************/
	void allocate_BOL_EOF(final CSpec spec) {
		CUtility.ASSERT(CSpec.NUM_PSEUDO == 2);
		spec.BOL = spec.m_dtrans_ncols++;
		spec.EOF = spec.m_dtrans_ncols++;
	}

	/***************************************************************
	 Function: cat_expr
	 Description: Recursive descent regular expression parser.
	 **************************************************************/
	private void cat_expr(final CNfaPair pair) throws java.io.IOException {
		CNfaPair e2_pair;

		if (CUtility.DESCENT_DEBUG) {
			CUtility.enter(
				"cat_expr",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != pair);
		}

		e2_pair = CAlloc.newCNfaPair();

		if (this.first_in_cat(this.m_spec.m_current_token)) {
			this.factor(pair);
		}

		while (this.first_in_cat(this.m_spec.m_current_token)) {
			this.factor(e2_pair);

			/* Destroy */
			pair.m_end.mimic(e2_pair.m_start);
			this.discardCNfa(e2_pair.m_start);

			pair.m_end = e2_pair.m_end;
		}

		if (CUtility.DESCENT_DEBUG) {
			CUtility.leave(
				"cat_expr",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}
	}

	/***************************************************************
	 Function: discardCNfa
	 Description: 
	 **************************************************************/
	private void discardCNfa(final CNfa nfa) {
		this.m_spec.m_nfa_states.removeElement(nfa);
	}

	/***************************************************************
	 Function: dodash
	 Description: Recursive descent regular expression parser.
	 **************************************************************/
	private void dodash(final CSet set) throws java.io.IOException {
		int first = -1;

		if (CUtility.DESCENT_DEBUG) {
			CUtility.enter(
				"dodash",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		while (CLexGen.EOS != this.m_spec.m_current_token
				&& CLexGen.CCL_END != this.m_spec.m_current_token) {
			// DASH loses its special meaning if it is first in class.
			if (CLexGen.DASH == this.m_spec.m_current_token && -1 != first) {
				this.m_lexGen.advance();
				// DASH loses its special meaning if it is last in class.
				if (this.m_spec.m_current_token == CLexGen.CCL_END) {
					// 'first' already in set.
					set.add('-');
					break;
				}
				for (; first <= this.m_spec.m_lexeme; ++first) {
					if (this.m_spec.m_ignorecase) {
						set.addncase((char) first);
					}
					else {
						set.add(first);
					}
				}
			}
			else {
				first = this.m_spec.m_lexeme;
				if (this.m_spec.m_ignorecase) {
					set.addncase(this.m_spec.m_lexeme);
				}
				else {
					set.add(this.m_spec.m_lexeme);
				}
			}

			this.m_lexGen.advance();
		}

		if (CUtility.DESCENT_DEBUG) {
			CUtility.leave(
				"dodash",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}
	}

	/***************************************************************
	 Function: expr
	 Description: Recursive descent regular expression parser.
	 **************************************************************/
	private void expr(final CNfaPair pair) throws java.io.IOException {
		CNfaPair e2_pair;
		CNfa p;

		if (CUtility.DESCENT_DEBUG) {
			CUtility.enter(
				"expr",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != pair);
		}

		e2_pair = CAlloc.newCNfaPair();

		this.cat_expr(pair);

		while (CLexGen.OR == this.m_spec.m_current_token) {
			this.m_lexGen.advance();
			this.cat_expr(e2_pair);

			p = CAlloc.newCNfa(this.m_spec);
			p.m_next2 = e2_pair.m_start;
			p.m_next = pair.m_start;
			pair.m_start = p;

			p = CAlloc.newCNfa(this.m_spec);
			pair.m_end.m_next = p;
			e2_pair.m_end.m_next = p;
			pair.m_end = p;
		}

		if (CUtility.DESCENT_DEBUG) {
			CUtility.leave(
				"expr",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}
	}

	/***************************************************************
	 Function: factor
	 Description: Recursive descent regular expression parser.
	 **************************************************************/
	private void factor(final CNfaPair pair) throws java.io.IOException {
		CNfa start = null;
		CNfa end = null;

		if (CUtility.DESCENT_DEBUG) {
			CUtility.enter(
				"factor",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		this.term(pair);

		if (CLexGen.CLOSURE == this.m_spec.m_current_token
				|| CLexGen.PLUS_CLOSE == this.m_spec.m_current_token
				|| CLexGen.OPTIONAL == this.m_spec.m_current_token) {
			start = CAlloc.newCNfa(this.m_spec);
			end = CAlloc.newCNfa(this.m_spec);

			start.m_next = pair.m_start;
			pair.m_end.m_next = end;

			if (CLexGen.CLOSURE == this.m_spec.m_current_token
					|| CLexGen.OPTIONAL == this.m_spec.m_current_token) {
				start.m_next2 = end;
			}

			if (CLexGen.CLOSURE == this.m_spec.m_current_token
					|| CLexGen.PLUS_CLOSE == this.m_spec.m_current_token) {
				pair.m_end.m_next2 = pair.m_start;
			}

			pair.m_start = start;
			pair.m_end = end;
			this.m_lexGen.advance();
		}

		if (CUtility.DESCENT_DEBUG) {
			CUtility.leave(
				"factor",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}
	}

	/***************************************************************
	 Function: first_in_cat
	 Description: Recursive descent regular expression parser.
	 **************************************************************/
	private boolean first_in_cat(final int token) {
		switch (token) {
			case CLexGen.CLOSE_PAREN :
			case CLexGen.AT_EOL :
			case CLexGen.OR :
			case CLexGen.EOS :
				return false;

			case CLexGen.CLOSURE :
			case CLexGen.PLUS_CLOSE :
			case CLexGen.OPTIONAL :
				CError.parse_error(CError.E_CLOSE, this.m_input.m_line_number);
				return false;

			case CLexGen.CCL_END :
				CError
					.parse_error(CError.E_BRACKET, this.m_input.m_line_number);
				return false;

			case CLexGen.AT_BOL :
				CError.parse_error(CError.E_BOL, this.m_input.m_line_number);
				return false;

			default :
				break;
		}

		return true;
	}

	/***************************************************************
	 Function: machine
	 Description: Recursive descent regular expression parser.
	 **************************************************************/
	private CNfa machine() throws java.io.IOException {
		CNfa start;
		CNfa p;
		SparseBitSet states;

		if (CUtility.DESCENT_DEBUG) {
			CUtility.enter(
				"machine",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		start = CAlloc.newCNfa(this.m_spec);
		p = start;

		states = this.m_lexGen.getStates();

		/* Begin: Added for states. */
		this.m_spec.m_current_token = CLexGen.EOS;
		this.m_lexGen.advance();
		/* End: Added for states. */

		if (CLexGen.END_OF_INPUT != this.m_spec.m_current_token) // CSA fix.
		{
			p.m_next = this.rule();

			this.processStates(states, p.m_next);
		}

		while (CLexGen.END_OF_INPUT != this.m_spec.m_current_token) {
			/* Make state changes HERE. */
			states = this.m_lexGen.getStates();

			/* Begin: Added for states. */
			this.m_lexGen.advance();
			if (CLexGen.END_OF_INPUT == this.m_spec.m_current_token) {
				break;
			}
			/* End: Added for states. */

			p.m_next2 = CAlloc.newCNfa(this.m_spec);
			p = p.m_next2;
			p.m_next = this.rule();

			this.processStates(states, p.m_next);
		}

		// CSA: add pseudo-rules for BOL and EOF
		final SparseBitSet all_states = new SparseBitSet();
		for (int i = 0; i < this.m_spec.m_states.size(); ++i) {
			all_states.set(i);
		}
		p.m_next2 = CAlloc.newCNfa(this.m_spec);
		p = p.m_next2;
		p.m_next = CAlloc.newCNfa(this.m_spec);
		p.m_next.m_edge = CNfa.CCL;
		p.m_next.m_next = CAlloc.newCNfa(this.m_spec);
		p.m_next.m_set = new CSet();
		p.m_next.m_set.add(this.m_spec.BOL);
		p.m_next.m_set.add(this.m_spec.EOF);
		p.m_next.m_next.m_accept = // do-nothing accept rule
			new CAccept(new char[0], 0, this.m_input.m_line_number + 1);
		this.processStates(all_states, p.m_next);
		// CSA: done. 

		if (CUtility.DESCENT_DEBUG) {
			CUtility.leave(
				"machine",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		return start;
	}

	/***************************************************************
	 Function: processStates
	 Description:
	 **************************************************************/
	private void processStates(final SparseBitSet states, final CNfa current) {
		int size;
		int i;

		size = this.m_spec.m_states.size();
		for (i = 0; i < size; ++i) {
			if (states.get(i)) {
				this.m_spec.m_state_rules[i].addElement(current);
			}
		}
	}

	/***************************************************************
	 Function: reset
	 Description: Resets CMakeNfa member variables.
	 **************************************************************/
	private void reset() {
		this.m_input = null;
		this.m_lexGen = null;
		this.m_spec = null;
	}

	/***************************************************************
	 Function: rule
	 Description: Recursive descent regular expression parser.
	 **************************************************************/
	private CNfa rule() throws java.io.IOException {
		CNfaPair pair;
		final CNfa p;
		CNfa start = null;
		CNfa end = null;
		int anchor = CSpec.NONE;

		if (CUtility.DESCENT_DEBUG) {
			CUtility.enter(
				"rule",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		pair = CAlloc.newCNfaPair();

		if (CLexGen.AT_BOL == this.m_spec.m_current_token) {
			anchor = anchor | CSpec.START;
			this.m_lexGen.advance();
			this.expr(pair);

			// CSA: fixed beginning-of-line operator. 8-aug-1999
			start = CAlloc.newCNfa(this.m_spec);
			start.m_edge = this.m_spec.BOL;
			start.m_next = pair.m_start;
			end = pair.m_end;
		}
		else {
			this.expr(pair);
			start = pair.m_start;
			end = pair.m_end;
		}

		if (CLexGen.AT_EOL == this.m_spec.m_current_token) {
			this.m_lexGen.advance();
			// CSA: fixed end-of-line operator. 8-aug-1999
			final CNfaPair nlpair = CAlloc.newNLPair(this.m_spec);
			end.m_next = CAlloc.newCNfa(this.m_spec);
			end.m_next.m_next = nlpair.m_start;
			end.m_next.m_next2 = CAlloc.newCNfa(this.m_spec);
			end.m_next.m_next2.m_edge = this.m_spec.EOF;
			end.m_next.m_next2.m_next = nlpair.m_end;
			end = nlpair.m_end;
			anchor = anchor | CSpec.END;
		}

		/* Check for null rules. Charles Fischer found this bug. [CSA] */
		if (end == null) {
			CError.parse_error(CError.E_ZERO, this.m_input.m_line_number);
		}

		/* Handle end of regular expression.  See page 103. */
		end.m_accept = this.m_lexGen.packAccept();
		end.m_anchor = anchor;

		/* Begin: Removed for states. */
		/*m_lexGen.advance();*/
		/* End: Removed for states. */

		if (CUtility.DESCENT_DEBUG) {
			CUtility.leave(
				"rule",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		return start;
	}

	/***************************************************************
	 Function: set
	 Description: Sets CMakeNfa member variables.
	 **************************************************************/
	private void set(final CLexGen lexGen, final CSpec spec, final CInput input) {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != input);
			CUtility.ASSERT(null != lexGen);
			CUtility.ASSERT(null != spec);
		}

		this.m_input = input;
		this.m_lexGen = lexGen;
		this.m_spec = spec;
	}

	/***************************************************************
	 Function: term
	 Description: Recursive descent regular expression parser.
	 **************************************************************/
	private void term(final CNfaPair pair) throws java.io.IOException {
		CNfa start;
		boolean isAlphaL;
		final int c;

		if (CUtility.DESCENT_DEBUG) {
			CUtility.enter(
				"term",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}

		if (CLexGen.OPEN_PAREN == this.m_spec.m_current_token) {
			this.m_lexGen.advance();
			this.expr(pair);

			if (CLexGen.CLOSE_PAREN == this.m_spec.m_current_token) {
				this.m_lexGen.advance();
			}
			else {
				CError.parse_error(CError.E_SYNTAX, this.m_input.m_line_number);
			}
		}
		else {
			start = CAlloc.newCNfa(this.m_spec);
			pair.m_start = start;

			start.m_next = CAlloc.newCNfa(this.m_spec);
			pair.m_end = start.m_next;

			if (CLexGen.L == this.m_spec.m_current_token
					&& Character.isLetter(this.m_spec.m_lexeme)) {
				isAlphaL = true;
			}
			else {
				isAlphaL = false;
			}
			if (false == (CLexGen.ANY == this.m_spec.m_current_token
					|| CLexGen.CCL_START == this.m_spec.m_current_token || this.m_spec.m_ignorecase
					&& isAlphaL)) {
				start.m_edge = this.m_spec.m_lexeme;
				this.m_lexGen.advance();
			}
			else {
				start.m_edge = CNfa.CCL;

				start.m_set = new CSet();

				/* Match case-insensitive letters using character class. */
				if (this.m_spec.m_ignorecase && isAlphaL) {
					start.m_set.addncase(this.m_spec.m_lexeme);
				}
				/* Match dot (.) using character class. */
				else if (CLexGen.ANY == this.m_spec.m_current_token) {
					start.m_set.add('\n');
					start.m_set.add('\r');
					// CSA: exclude BOL and EOF from character classes
					start.m_set.add(this.m_spec.BOL);
					start.m_set.add(this.m_spec.EOF);
					start.m_set.complement();
				}
				else {
					this.m_lexGen.advance();
					if (CLexGen.AT_BOL == this.m_spec.m_current_token) {
						this.m_lexGen.advance();

						// CSA: exclude BOL and EOF from character classes
						start.m_set.add(this.m_spec.BOL);
						start.m_set.add(this.m_spec.EOF);
						start.m_set.complement();
					}
					if (false == (CLexGen.CCL_END == this.m_spec.m_current_token)) {
						this.dodash(start.m_set);
					}
					/*else
					 {
					 for (c = 0; c <= ' '; ++c)
					 {
					 start.m_set.add((byte) c);
					 }
					 }*/
				}
				this.m_lexGen.advance();
			}
		}

		if (CUtility.DESCENT_DEBUG) {
			CUtility.leave(
				"term",
				this.m_spec.m_lexeme,
				this.m_spec.m_current_token);
		}
	}

	/***************************************************************
	 Function: thompson
	 Description: High level access function to module.
	 Deposits result in input CSpec.
	 **************************************************************/
	void thompson(final CLexGen lexGen, final CSpec spec, final CInput input)
			throws java.io.IOException {
		int i;
		CNfa elem;
		int size;

		/* Set member variables. */
		this.reset();
		this.set(lexGen, spec, input);

		size = this.m_spec.m_states.size();
		this.m_spec.m_state_rules = new Vector[size];
		for (i = 0; i < size; ++i) {
			this.m_spec.m_state_rules[i] = new Vector();
		}

		/* Initialize current token variable 
		 and create nfa. */
		/*m_spec.m_current_token = m_lexGen.EOS;
		 m_lexGen.advance();*/

		this.m_spec.m_nfa_start = this.machine();

		/* Set labels in created nfa machine. */
		size = this.m_spec.m_nfa_states.size();
		for (i = 0; i < size; ++i) {
			elem = (CNfa) this.m_spec.m_nfa_states.elementAt(i);
			elem.m_label = i;
		}

		/* Debugging output. */
		if (CUtility.DO_DEBUG) {
			this.m_lexGen.print_nfa();
		}

		if (this.m_spec.m_verbose) {
			System.out.println("NFA comprised of "
					+ (this.m_spec.m_nfa_states.size() + 1) + " states.");
		}

		this.reset();
	}
}

/***************************************************************
 Class: CMinimize
 **************************************************************/
class CMinimize {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	CSpec m_spec;
	Vector m_group;
	int m_ingroup[];

	/***************************************************************
	 Function: CMinimize
	 Description: Constructor.
	 **************************************************************/
	CMinimize() {
		this.reset();
	}

	/***************************************************************
	 Function: col_copy
	 Description: Copies source column into destination column.
	 **************************************************************/
	private void col_copy(final int dest, final int src) {
		int n;
		int i;
		CDTrans dtrans;

		n = this.m_spec.m_dtrans_vector.size();
		for (i = 0; i < n; ++i) {
			dtrans = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(i);
			dtrans.m_dtrans[dest] = dtrans.m_dtrans[src];
		}
	}

	/***************************************************************
	 Function: col_equiv
	 Description: 
	 **************************************************************/
	private boolean col_equiv(final int col1, final int col2) {
		int n;
		int i;
		CDTrans dtrans;

		n = this.m_spec.m_dtrans_vector.size();
		for (i = 0; i < n; ++i) {
			dtrans = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(i);
			if (dtrans.m_dtrans[col1] != dtrans.m_dtrans[col2]) {
				return false;
			}
		}

		return true;
	}

	/***************************************************************
	 Function: fix_dtrans
	 Description: Updates CDTrans table after minimization 
	 using groups, removing redundant transition table states.
	 **************************************************************/
	private void fix_dtrans() {
		Vector new_vector;
		int i;
		int size;
		Vector dtrans_group;
		CDTrans first;
		int c;

		new_vector = new Vector();

		size = this.m_spec.m_state_dtrans.length;
		for (i = 0; i < size; ++i) {
			if (CDTrans.F != this.m_spec.m_state_dtrans[i]) {
				this.m_spec.m_state_dtrans[i] =
					this.m_ingroup[this.m_spec.m_state_dtrans[i]];
			}
		}

		size = this.m_group.size();
		for (i = 0; i < size; ++i) {
			dtrans_group = (Vector) this.m_group.elementAt(i);
			first = (CDTrans) dtrans_group.elementAt(0);
			new_vector.addElement(first);

			for (c = 0; c < this.m_spec.m_dtrans_ncols; ++c) {
				if (CDTrans.F != first.m_dtrans[c]) {
					first.m_dtrans[c] = this.m_ingroup[first.m_dtrans[c]];
				}
			}
		}

		this.m_group = null;
		this.m_spec.m_dtrans_vector = new_vector;
	}

	/***************************************************************
	 Function: init_groups
	 Description:
	 **************************************************************/
	private void init_groups() {
		int i;
		int j;
		int group_count;
		int size;
		final CAccept accept;
		CDTrans dtrans;
		Vector dtrans_group;
		CDTrans first;
		boolean group_found;

		this.m_group = new Vector();
		group_count = 0;

		size = this.m_spec.m_dtrans_vector.size();
		this.m_ingroup = new int[size];

		for (i = 0; i < size; ++i) {
			group_found = false;
			dtrans = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(i);

			if (CUtility.DEBUG) {
				CUtility.ASSERT(i == dtrans.m_label);
				CUtility.ASSERT(false == group_found);
				CUtility.ASSERT(group_count == this.m_group.size());
			}

			for (j = 0; j < group_count; ++j) {
				dtrans_group = (Vector) this.m_group.elementAt(j);

				if (CUtility.DEBUG) {
					CUtility.ASSERT(false == group_found);
					CUtility.ASSERT(0 < dtrans_group.size());
				}

				first = (CDTrans) dtrans_group.elementAt(0);

				if (CUtility.SLOW_DEBUG) {
					CDTrans check;
					int k;
					int s;

					s = dtrans_group.size();
					CUtility.ASSERT(0 < s);

					for (k = 1; k < s; ++k) {
						check = (CDTrans) dtrans_group.elementAt(k);
						CUtility.ASSERT(check.m_accept == first.m_accept);
					}
				}

				if (first.m_accept == dtrans.m_accept) {
					dtrans_group.addElement(dtrans);
					this.m_ingroup[i] = j;
					group_found = true;

					if (CUtility.DEBUG) {
						CUtility.ASSERT(j == this.m_ingroup[dtrans.m_label]);
					}

					break;
				}
			}

			if (false == group_found) {
				dtrans_group = new Vector();
				dtrans_group.addElement(dtrans);
				this.m_ingroup[i] = this.m_group.size();
				this.m_group.addElement(dtrans_group);
				++group_count;
			}
		}

		if (this.m_spec.m_verbose && true == CUtility.OLD_DUMP_DEBUG) {
			System.out.println("Initial grouping:");
			this.pgroups();
			System.out.println();
		}
	}

	/***************************************************************
	 Function: min_dfa
	 Description: High-level access function to module.
	 **************************************************************/
	void min_dfa(final CSpec spec) {
		this.set(spec);

		/* Remove redundant states. */
		this.minimize();

		/* Column and row compression. 
		 Save accept states in auxilary vector. */
		this.reduce();

		this.reset();
	}
	/***************************************************************
	 Function: minimize
	 Description: Removes redundant transition table states.
	 **************************************************************/
	private void minimize() {
		Vector dtrans_group;
		Vector new_group;
		int i;
		int j;
		int old_group_count;
		int group_count;
		CDTrans next;
		CDTrans first;
		int goto_first;
		int goto_next;
		int c;
		int group_size;
		boolean added;

		this.init_groups();

		group_count = this.m_group.size();
		old_group_count = group_count - 1;

		while (old_group_count != group_count) {
			old_group_count = group_count;

			if (CUtility.DEBUG) {
				CUtility.ASSERT(this.m_group.size() == group_count);
			}

			for (i = 0; i < group_count; ++i) {
				dtrans_group = (Vector) this.m_group.elementAt(i);

				group_size = dtrans_group.size();
				if (group_size <= 1) {
					continue;
				}

				new_group = new Vector();
				added = false;

				first = (CDTrans) dtrans_group.elementAt(0);
				for (j = 1; j < group_size; ++j) {
					next = (CDTrans) dtrans_group.elementAt(j);

					for (c = 0; c < this.m_spec.m_dtrans_ncols; ++c) {
						goto_first = first.m_dtrans[c];
						goto_next = next.m_dtrans[c];

						if (goto_first != goto_next
								&& (goto_first == CDTrans.F
										|| goto_next == CDTrans.F || this.m_ingroup[goto_next] != this.m_ingroup[goto_first])) {
							if (CUtility.DEBUG) {
								CUtility
									.ASSERT(dtrans_group.elementAt(j) == next);
							}

							dtrans_group.removeElementAt(j);
							--j;
							--group_size;
							new_group.addElement(next);
							if (false == added) {
								added = true;
								++group_count;
								this.m_group.addElement(new_group);
							}
							this.m_ingroup[next.m_label] =
								this.m_group.size() - 1;

							if (CUtility.DEBUG) {
								CUtility.ASSERT(this.m_group
									.contains(new_group) == true);
								CUtility.ASSERT(this.m_group
									.contains(dtrans_group) == true);
								CUtility
									.ASSERT(dtrans_group.contains(first) == true);
								CUtility
									.ASSERT(dtrans_group.contains(next) == false);
								CUtility
									.ASSERT(new_group.contains(first) == false);
								CUtility
									.ASSERT(new_group.contains(next) == true);
								CUtility
									.ASSERT(dtrans_group.size() == group_size);
								CUtility
									.ASSERT(i == this.m_ingroup[first.m_label]);
								CUtility
									.ASSERT(this.m_group.size() - 1 == this.m_ingroup[next.m_label]);
							}

							break;
						}
					}
				}
			}
		}

		System.out.println(this.m_group.size()
				+ " states after removal of redundant states.");

		if (this.m_spec.m_verbose && true == CUtility.OLD_DUMP_DEBUG) {
			System.out.println();
			System.out.println("States grouped as follows after minimization");
			this.pgroups();
		}

		this.fix_dtrans();
	}

	/***************************************************************
	 Function: pgroups
	 **************************************************************/
	private void pgroups() {
		int i;
		int dtrans_size;
		int group_size;

		group_size = this.m_group.size();
		for (i = 0; i < group_size; ++i) {
			System.out.print("\tGroup " + i + " {");
			this.pset((Vector) this.m_group.elementAt(i));
			System.out.println("}");
			System.out.println();
		}

		System.out.println();
		dtrans_size = this.m_spec.m_dtrans_vector.size();
		for (i = 0; i < dtrans_size; ++i) {
			System.out.println("\tstate " + i + " is in group "
					+ this.m_ingroup[i]);
		}
	}

	/***************************************************************
	 Function: pset
	 **************************************************************/
	private void pset(final Vector dtrans_group) {
		int i;
		int size;
		CDTrans dtrans;

		size = dtrans_group.size();
		for (i = 0; i < size; ++i) {
			dtrans = (CDTrans) dtrans_group.elementAt(i);
			System.out.print(dtrans.m_label + " ");
		}
	}

	/***************************************************************
	 Function: reduce
	 Description: 
	 **************************************************************/
	private void reduce() {
		int i;
		int j;
		int k;
		int nrows;
		int reduced_ncols;
		int reduced_nrows;
		SparseBitSet set;
		CDTrans dtrans;
		int size;

		set = new SparseBitSet();

		/* Save accept nodes and anchor entries. */
		size = this.m_spec.m_dtrans_vector.size();
		this.m_spec.m_anchor_array = new int[size];
		this.m_spec.m_accept_vector = new Vector();
		for (i = 0; i < size; ++i) {
			dtrans = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(i);
			this.m_spec.m_accept_vector.addElement(dtrans.m_accept);
			this.m_spec.m_anchor_array[i] = dtrans.m_anchor;
			dtrans.m_accept = null;
		}

		/* Allocate column map. */
		this.m_spec.m_col_map = new int[this.m_spec.m_dtrans_ncols];
		for (i = 0; i < this.m_spec.m_dtrans_ncols; ++i) {
			this.m_spec.m_col_map[i] = -1;
		}

		/* Process columns for reduction. */
		for (reduced_ncols = 0;; ++reduced_ncols) {
			if (CUtility.DEBUG) {
				for (i = 0; i < reduced_ncols; ++i) {
					CUtility.ASSERT(-1 != this.m_spec.m_col_map[i]);
				}
			}

			for (i = reduced_ncols; i < this.m_spec.m_dtrans_ncols; ++i) {
				if (-1 == this.m_spec.m_col_map[i]) {
					break;
				}
			}

			if (i >= this.m_spec.m_dtrans_ncols) {
				break;
			}

			if (CUtility.DEBUG) {
				CUtility.ASSERT(false == set.get(i));
				CUtility.ASSERT(-1 == this.m_spec.m_col_map[i]);
			}

			set.set(i);

			this.m_spec.m_col_map[i] = reduced_ncols;

			/* UNDONE: Optimize by doing all comparisons in one batch. */
			for (j = i + 1; j < this.m_spec.m_dtrans_ncols; ++j) {
				if (-1 == this.m_spec.m_col_map[j]
						&& true == this.col_equiv(i, j)) {
					this.m_spec.m_col_map[j] = reduced_ncols;
				}
			}
		}

		/* Reduce columns. */
		k = 0;
		for (i = 0; i < this.m_spec.m_dtrans_ncols; ++i) {
			if (set.get(i)) {
				++k;

				set.clear(i);

				j = this.m_spec.m_col_map[i];

				if (CUtility.DEBUG) {
					CUtility.ASSERT(j <= i);
				}

				if (j == i) {
					continue;
				}

				this.col_copy(j, i);
			}
		}
		this.m_spec.m_dtrans_ncols = reduced_ncols;
		/* truncate m_dtrans at proper length (freeing extra) */
		this.trunc_col();

		if (CUtility.DEBUG) {
			CUtility.ASSERT(k == reduced_ncols);
		}

		/* Allocate row map. */
		nrows = this.m_spec.m_dtrans_vector.size();
		this.m_spec.m_row_map = new int[nrows];
		for (i = 0; i < nrows; ++i) {
			this.m_spec.m_row_map[i] = -1;
		}

		/* Process rows to reduce. */
		for (reduced_nrows = 0;; ++reduced_nrows) {
			if (CUtility.DEBUG) {
				for (i = 0; i < reduced_nrows; ++i) {
					CUtility.ASSERT(-1 != this.m_spec.m_row_map[i]);
				}
			}

			for (i = reduced_nrows; i < nrows; ++i) {
				if (-1 == this.m_spec.m_row_map[i]) {
					break;
				}
			}

			if (i >= nrows) {
				break;
			}

			if (CUtility.DEBUG) {
				CUtility.ASSERT(false == set.get(i));
				CUtility.ASSERT(-1 == this.m_spec.m_row_map[i]);
			}

			set.set(i);

			this.m_spec.m_row_map[i] = reduced_nrows;

			/* UNDONE: Optimize by doing all comparisons in one batch. */
			for (j = i + 1; j < nrows; ++j) {
				if (-1 == this.m_spec.m_row_map[j]
						&& true == this.row_equiv(i, j)) {
					this.m_spec.m_row_map[j] = reduced_nrows;
				}
			}
		}

		/* Reduce rows. */
		k = 0;
		for (i = 0; i < nrows; ++i) {
			if (set.get(i)) {
				++k;

				set.clear(i);

				j = this.m_spec.m_row_map[i];

				if (CUtility.DEBUG) {
					CUtility.ASSERT(j <= i);
				}

				if (j == i) {
					continue;
				}

				this.row_copy(j, i);
			}
		}
		this.m_spec.m_dtrans_vector.setSize(reduced_nrows);

		if (CUtility.DEBUG) {
			/*System.out.println("k = " + k + "\nreduced_nrows = " + reduced_nrows + "");*/
			CUtility.ASSERT(k == reduced_nrows);
		}
	}

	/***************************************************************
	 Function: reset
	 Description: Resets member variables.
	 **************************************************************/
	private void reset() {
		this.m_spec = null;
		this.m_group = null;
		this.m_ingroup = null;
	}

	/***************************************************************
	 Function: row_copy
	 Description: Copies source row into destination row.
	 **************************************************************/
	private void row_copy(final int dest, final int src) {
		CDTrans dtrans;

		dtrans = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(src);
		this.m_spec.m_dtrans_vector.setElementAt(dtrans, dest);
	}

	/***************************************************************
	 Function: row_equiv
	 Description: 
	 **************************************************************/
	private boolean row_equiv(final int row1, final int row2) {
		int i;
		CDTrans dtrans1;
		CDTrans dtrans2;

		dtrans1 = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(row1);
		dtrans2 = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(row2);

		for (i = 0; i < this.m_spec.m_dtrans_ncols; ++i) {
			if (dtrans1.m_dtrans[i] != dtrans2.m_dtrans[i]) {
				return false;
			}
		}

		return true;
	}

	/***************************************************************
	 Function: set
	 Description: Sets member variables.
	 **************************************************************/
	private void set(final CSpec spec) {
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != spec);
		}

		this.m_spec = spec;
		this.m_group = null;
		this.m_ingroup = null;
	}

	/***************************************************************
	 Function: trunc_col
	 Description: Truncates each column to the 'correct' length.
	 **************************************************************/
	private void trunc_col() {
		int n;
		int i;
		CDTrans dtrans;

		n = this.m_spec.m_dtrans_vector.size();
		for (i = 0; i < n; ++i) {
			final int[] ndtrans = new int[this.m_spec.m_dtrans_ncols];
			dtrans = (CDTrans) this.m_spec.m_dtrans_vector.elementAt(i);
			System.arraycopy(dtrans.m_dtrans, 0, ndtrans, 0, ndtrans.length);
			dtrans.m_dtrans = ndtrans;
		}
	}
}

/********************************************************
 Class: CNfa
 *******************************************************/
class CNfa {
	/********************************************************
	 Member Variables
	 *******************************************************/
	int m_edge; /* Label for edge type:
				character code, 
				CCL (character class), 
				[STATE,
				SCL (state class),]
				EMPTY, 
				EPSILON. */

	CSet m_set; /* Set to store character classes. */
	CNfa m_next; /* Next state (or null if none). */

	CNfa m_next2; /* Another state with type == EPSILON
					and null if not used.  
					The NFA construction should result in two
					outgoing edges only if both are EPSILON edges. */

	CAccept m_accept; /* Set to null if nonaccepting state. */
	int m_anchor; /* Says if and where pattern is anchored. */

	int m_label;

	SparseBitSet m_states;

	/********************************************************
	 Constants
	 *******************************************************/
	static final int NO_LABEL = -1;

	/********************************************************
	 Constants: Edge Types
	 Note: Edge transitions on one specific character
	 are labelled with the character Ascii (Unicode)
	 codes.  So none of the constants below should
	 overlap with the natural character codes.
	 *******************************************************/
	static final int CCL = -1;
	static final int EMPTY = -2;
	static final int EPSILON = -3;

	/********************************************************
	 Function: CNfa
	 *******************************************************/
	CNfa() {
		this.m_edge = CNfa.EMPTY;
		this.m_set = null;
		this.m_next = null;
		this.m_next2 = null;
		this.m_accept = null;
		this.m_anchor = CSpec.NONE;
		this.m_label = CNfa.NO_LABEL;
		this.m_states = null;
	}

	/********************************************************
	 Function: mimic
	 Description: Converts this NFA state into a copy of
	 the input one.
	 *******************************************************/
	void mimic(final CNfa nfa) {
		this.m_edge = nfa.m_edge;

		if (null != nfa.m_set) {
			if (null == this.m_set) {
				this.m_set = new CSet();
			}
			this.m_set.mimic(nfa.m_set);
		}
		else {
			this.m_set = null;
		}

		this.m_next = nfa.m_next;
		this.m_next2 = nfa.m_next2;
		this.m_accept = nfa.m_accept;
		this.m_anchor = nfa.m_anchor;

		if (null != nfa.m_states) {
			this.m_states = (SparseBitSet) nfa.m_states.clone();
		}
		else {
			this.m_states = null;
		}
	}
}

/***************************************************************
 Class: CNfa2Dfa
 **************************************************************/
class CNfa2Dfa {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	private CSpec m_spec;
	private int m_unmarked_dfa;
	private CLexGen m_lexGen;

	/***************************************************************
	 Constants
	 **************************************************************/
	private static final int NOT_IN_DSTATES = -1;

	/***************************************************************
	 Function: CNfa2Dfa
	 **************************************************************/
	CNfa2Dfa() {
		this.reset();
	}

	/***************************************************************
	 function: add_to_dstates
	 Description: Takes as input a CBunch with details of
	 a dfa state that needs to be created.
	 1) Allocates a new dfa state and saves it in 
	 the appropriate CSpec vector.
	 2) Initializes the fields of the dfa state
	 with the information in the CBunch.
	 3) Returns index of new dfa.
	 **************************************************************/
	private int add_to_dstates(final CBunch bunch) {
		CDfa dfa;

		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != bunch.m_nfa_set);
			CUtility.ASSERT(null != bunch.m_nfa_bit);
			CUtility.ASSERT(null != bunch.m_accept
					|| CSpec.NONE == bunch.m_anchor);
		}

		/* Allocate, passing CSpec so dfa label can be set. */
		dfa = CAlloc.newCDfa(this.m_spec);

		/* Initialize fields, including the mark field. */
		dfa.m_nfa_set = (Vector) bunch.m_nfa_set.clone();
		dfa.m_nfa_bit = (SparseBitSet) bunch.m_nfa_bit.clone();
		dfa.m_accept = bunch.m_accept;
		dfa.m_anchor = bunch.m_anchor;
		dfa.m_mark = false;

		/* Register dfa state using BitSet in CSpec Hashtable. */
		this.m_spec.m_dfa_sets.put(dfa.m_nfa_bit, dfa);
		/*registerCDfa(dfa);*/

		if (CUtility.OLD_DUMP_DEBUG) {
			System.out.print("Registering set : ");
			this.m_lexGen.print_set(dfa.m_nfa_set);
			System.out.println();
		}

		return dfa.m_label;
	}

	/***************************************************************
	 Function: e_closure
	 Description: Alters and returns input set.
	 **************************************************************/
	private void e_closure(final CBunch bunch) {
		Stack nfa_stack;
		int size;
		int i;
		CNfa state;

		/* Debug checks. */
		if (CUtility.DEBUG) {
			CUtility.ASSERT(null != bunch);
			CUtility.ASSERT(null != bunch.m_nfa_set);
			CUtility.ASSERT(null != bunch.m_nfa_bit);
		}

		bunch.m_accept = null;
		bunch.m_anchor = CSpec.NONE;
		bunch.m_accept_index = CUtility.INT_MAX;

		/* Create initial stack. */
		nfa_stack = new Stack();
		size = bunch.m_nfa_set.size();
		for (i = 0; i < size; ++i) {
			state = (CNfa) bunch.m_nfa_set.elementAt(i);

			if (CUtility.DEBUG) {
				CUtility.ASSERT(bunch.m_nfa_bit.get(state.m_label));
			}

			nfa_stack.push(state);
		}

		/* Main loop. */
		while (false == nfa_stack.empty()) {
			state = (CNfa) nfa_stack.pop();

			if (CUtility.OLD_DUMP_DEBUG) {
				if (null != state.m_accept) {
					System.out.println("Looking at accepting state "
							+ state.m_label
							+ " with <"
							+ new String(
								state.m_accept.m_action,
								0,
								state.m_accept.m_action_read) + ">");
				}
			}

			if (null != state.m_accept && state.m_label < bunch.m_accept_index) {
				bunch.m_accept_index = state.m_label;
				bunch.m_accept = state.m_accept;
				bunch.m_anchor = state.m_anchor;

				if (CUtility.OLD_DUMP_DEBUG) {
					System.out.println("Found accepting state "
							+ state.m_label
							+ " with <"
							+ new String(
								state.m_accept.m_action,
								0,
								state.m_accept.m_action_read) + ">");
				}

				if (CUtility.DEBUG) {
					CUtility.ASSERT(null != bunch.m_accept);
					CUtility.ASSERT(CSpec.NONE == bunch.m_anchor
							|| 0 != (bunch.m_anchor & CSpec.END)
							|| 0 != (bunch.m_anchor & CSpec.START));
				}
			}

			if (CNfa.EPSILON == state.m_edge) {
				if (null != state.m_next) {
					if (false == bunch.m_nfa_set.contains(state.m_next)) {
						if (CUtility.DEBUG) {
							CUtility.ASSERT(false == bunch.m_nfa_bit
								.get(state.m_next.m_label));
						}

						bunch.m_nfa_bit.set(state.m_next.m_label);
						bunch.m_nfa_set.addElement(state.m_next);
						nfa_stack.push(state.m_next);
					}
				}

				if (null != state.m_next2) {
					if (false == bunch.m_nfa_set.contains(state.m_next2)) {
						if (CUtility.DEBUG) {
							CUtility.ASSERT(false == bunch.m_nfa_bit
								.get(state.m_next2.m_label));
						}

						bunch.m_nfa_bit.set(state.m_next2.m_label);
						bunch.m_nfa_set.addElement(state.m_next2);
						nfa_stack.push(state.m_next2);
					}
				}
			}
		}

		if (null != bunch.m_nfa_set) {
			this.sortStates(bunch.m_nfa_set);
		}

		return;
	}

	/***************************************************************
	 Function: free_dfa_states
	 **************************************************************/
	private void free_dfa_states() {
		this.m_spec.m_dfa_states = null;
		this.m_spec.m_dfa_sets = null;
	}

	/***************************************************************
	 Function: free_nfa_states
	 **************************************************************/
	private void free_nfa_states() {
		/* UNDONE: Remove references to nfas from within dfas. */
		/* UNDONE: Don't free CAccepts. */

		this.m_spec.m_nfa_states = null;
		this.m_spec.m_nfa_start = null;
		this.m_spec.m_state_rules = null;
	}

	/***************************************************************
	 Function: get_unmarked
	 Description: Returns next unmarked DFA state.
	 **************************************************************/
	private CDfa get_unmarked() {
		int size;
		CDfa dfa;

		size = this.m_spec.m_dfa_states.size();
		while (this.m_unmarked_dfa < size) {
			dfa =
				(CDfa) this.m_spec.m_dfa_states.elementAt(this.m_unmarked_dfa);

			if (false == dfa.m_mark) {
				if (CUtility.OLD_DUMP_DEBUG) {
					System.out.print("*");
					System.out.flush();
				}

				if (this.m_spec.m_verbose && true == CUtility.OLD_DUMP_DEBUG) {
					System.out.println("---------------");
					System.out.print("working on DFA state "
							+ this.m_unmarked_dfa + " = NFA states: ");
					this.m_lexGen.print_set(dfa.m_nfa_set);
					System.out.println();
				}

				return dfa;
			}

			++this.m_unmarked_dfa;
		}

		return null;
	}

	/***************************************************************
	 Function: in_dstates
	 **************************************************************/
	private int in_dstates(final CBunch bunch) {
		CDfa dfa;

		if (CUtility.OLD_DEBUG) {
			System.out.print("Looking for set : ");
			this.m_lexGen.print_set(bunch.m_nfa_set);
		}

		dfa = (CDfa) this.m_spec.m_dfa_sets.get(bunch.m_nfa_bit);

		if (null != dfa) {
			if (CUtility.OLD_DUMP_DEBUG) {
				System.out.println(" FOUND!");
			}

			return dfa.m_label;
		}

		if (CUtility.OLD_DUMP_DEBUG) {
			System.out.println(" NOT FOUND!");
		}
		return CNfa2Dfa.NOT_IN_DSTATES;
	}

	/***************************************************************
	 Function: make_dfa
	 Description: High-level access function to module.
	 **************************************************************/
	void make_dfa(final CLexGen lexGen, final CSpec spec) {
		final int i;

		this.reset();
		this.set(lexGen, spec);

		this.make_dtrans();
		this.free_nfa_states();

		if (this.m_spec.m_verbose && true == CUtility.OLD_DUMP_DEBUG) {
			System.out.println(this.m_spec.m_dfa_states.size()
					+ " DFA states in original machine.");
		}

		this.free_dfa_states();
	}

	/***************************************************************
	 Function: make_dtrans
	 Description: Creates uncompressed CDTrans transition table.
	 **************************************************************/
	private void make_dtrans()
	/* throws java.lang.CloneNotSupportedException*/
	{
		final CDfa next;
		CDfa dfa;
		CBunch bunch;
		int i;
		int nextstate;
		int size;
		CDTrans dtrans;
		CNfa nfa;
		int istate;
		int nstates;

		System.out.print("Working on DFA states.");

		/* Reference passing type and initializations. */
		bunch = new CBunch();
		this.m_unmarked_dfa = 0;

		/* Allocate mapping array. */
		nstates = this.m_spec.m_state_rules.length;
		this.m_spec.m_state_dtrans = new int[nstates];

		for (istate = 0; nstates > istate; ++istate) {
			/* CSA bugfix: if we skip all zero size rules, then
			 an specification with no rules produces an illegal
			 lexer (0 states) instead of a lexer that rejects
			 everything (1 nonaccepting state). [27-Jul-1999]
			 if (0 == m_spec.m_state_rules[istate].size())
			 {
			 m_spec.m_state_dtrans[istate] = CDTrans.F;
			 continue;
			 }
			 */

			/* Create start state and initialize fields. */
			bunch.m_nfa_set =
				(Vector) this.m_spec.m_state_rules[istate].clone();
			this.sortStates(bunch.m_nfa_set);

			bunch.m_nfa_bit = new SparseBitSet();

			/* Initialize bit set. */
			size = bunch.m_nfa_set.size();
			for (i = 0; size > i; ++i) {
				nfa = (CNfa) bunch.m_nfa_set.elementAt(i);
				bunch.m_nfa_bit.set(nfa.m_label);
			}

			bunch.m_accept = null;
			bunch.m_anchor = CSpec.NONE;
			bunch.m_accept_index = CUtility.INT_MAX;

			this.e_closure(bunch);
			this.add_to_dstates(bunch);

			this.m_spec.m_state_dtrans[istate] =
				this.m_spec.m_dtrans_vector.size();

			/* Main loop of CDTrans creation. */
			while (null != (dfa = this.get_unmarked())) {
				System.out.print(".");
				System.out.flush();

				if (CUtility.DEBUG) {
					CUtility.ASSERT(false == dfa.m_mark);
				}

				/* Get first unmarked node, then mark it. */
				dfa.m_mark = true;

				/* Allocate new CDTrans, then initialize fields. */
				dtrans =
					new CDTrans(this.m_spec.m_dtrans_vector.size(), this.m_spec);
				dtrans.m_accept = dfa.m_accept;
				dtrans.m_anchor = dfa.m_anchor;

				/* Set CDTrans array for each character transition. */
				for (i = 0; i < this.m_spec.m_dtrans_ncols; ++i) {
					if (CUtility.DEBUG) {
						CUtility.ASSERT(0 <= i);
						CUtility.ASSERT(this.m_spec.m_dtrans_ncols > i);
					}

					/* Create new dfa set by attempting character transition. */
					this.move(dfa.m_nfa_set, dfa.m_nfa_bit, i, bunch);
					if (null != bunch.m_nfa_set) {
						this.e_closure(bunch);
					}

					if (CUtility.DEBUG) {
						CUtility.ASSERT(null == bunch.m_nfa_set
								&& null == bunch.m_nfa_bit
								|| null != bunch.m_nfa_set
								&& null != bunch.m_nfa_bit);
					}

					/* Create new state or set state to empty. */
					if (null == bunch.m_nfa_set) {
						nextstate = CDTrans.F;
					}
					else {
						nextstate = this.in_dstates(bunch);

						if (CNfa2Dfa.NOT_IN_DSTATES == nextstate) {
							nextstate = this.add_to_dstates(bunch);
						}
					}

					if (CUtility.DEBUG) {
						CUtility.ASSERT(nextstate < this.m_spec.m_dfa_states
							.size());
					}

					dtrans.m_dtrans[i] = nextstate;
				}

				if (CUtility.DEBUG) {
					CUtility
						.ASSERT(this.m_spec.m_dtrans_vector.size() == dfa.m_label);
				}

				this.m_spec.m_dtrans_vector.addElement(dtrans);
			}
		}

		System.out.println();
	}

	/***************************************************************
	 Function: move
	 Description: Returns null if resulting NFA set is empty.
	 **************************************************************/
	void move(
		final Vector nfa_set,
		final SparseBitSet nfa_bit,
		final int b,
		final CBunch bunch) {
		int size;
		int index;
		CNfa state;

		bunch.m_nfa_set = null;
		bunch.m_nfa_bit = null;

		size = nfa_set.size();
		for (index = 0; index < size; ++index) {
			state = (CNfa) nfa_set.elementAt(index);

			if (b == state.m_edge || CNfa.CCL == state.m_edge
					&& true == state.m_set.contains(b)) {
				if (null == bunch.m_nfa_set) {
					if (CUtility.DEBUG) {
						CUtility.ASSERT(null == bunch.m_nfa_bit);
					}

					bunch.m_nfa_set = new Vector();
					/*bunch.m_nfa_bit 
					 = new SparseBitSet(m_spec.m_nfa_states.size());*/
					bunch.m_nfa_bit = new SparseBitSet();
				}

				bunch.m_nfa_set.addElement(state.m_next);
				/*System.out.println("Size of bitset: " + bunch.m_nfa_bit.size());
				 System.out.println("Reference index: " + state.m_next.m_label);
				 System.out.flush();*/
				bunch.m_nfa_bit.set(state.m_next.m_label);
			}
		}

		if (null != bunch.m_nfa_set) {
			if (CUtility.DEBUG) {
				CUtility.ASSERT(null != bunch.m_nfa_bit);
			}

			this.sortStates(bunch.m_nfa_set);
		}

		return;
	}

	/***************************************************************
	 Function: reset 
	 Description: 
	 **************************************************************/
	private void reset() {
		this.m_lexGen = null;
		this.m_spec = null;
		this.m_unmarked_dfa = 0;
	}

	/***************************************************************
	 Function: set 
	 Description: 
	 **************************************************************/
	private void set(final CLexGen lexGen, final CSpec spec) {
		this.m_lexGen = lexGen;
		this.m_spec = spec;
		this.m_unmarked_dfa = 0;
	}

	/***************************************************************
	 Function: sortStates
	 **************************************************************/
	private void sortStates(final Vector nfa_set) {
		CNfa elem;
		int begin;
		int size;
		int index;
		int value;
		int smallest_index;
		int smallest_value;
		CNfa begin_elem;

		size = nfa_set.size();
		for (begin = 0; begin < size; ++begin) {
			elem = (CNfa) nfa_set.elementAt(begin);
			smallest_value = elem.m_label;
			smallest_index = begin;

			for (index = begin + 1; index < size; ++index) {
				elem = (CNfa) nfa_set.elementAt(index);
				value = elem.m_label;

				if (value < smallest_value) {
					smallest_index = index;
					smallest_value = value;
				}
			}

			begin_elem = (CNfa) nfa_set.elementAt(begin);
			elem = (CNfa) nfa_set.elementAt(smallest_index);
			nfa_set.setElementAt(elem, begin);
			nfa_set.setElementAt(begin_elem, smallest_index);
		}

		if (CUtility.OLD_DEBUG) {
			System.out.print("NFA vector indices: ");

			for (index = 0; index < size; ++index) {
				elem = (CNfa) nfa_set.elementAt(index);
				System.out.print(elem.m_label + " ");
			}
			System.out.println();
		}

		return;
	}

}

/***************************************************************
 Class: CNfaPair
 **************************************************************/
class CNfaPair {
	/***************************************************************
	 Member Variables
	 **************************************************************/
	CNfa m_start;
	CNfa m_end;

	/***************************************************************
	 Function: CNfaPair
	 **************************************************************/
	CNfaPair() {
		this.m_start = null;
		this.m_end = null;
	}
}

/********************************************************
 Class: CSet
 *******************************************************/
class CSet {
	/********************************************************
	 Member Variables
	 *******************************************************/
	private SparseBitSet m_set;
	private boolean m_complement;

	/********************************************************
	 Function: CSet
	 *******************************************************/
	CSet() {
		this.m_set = new SparseBitSet();
		this.m_complement = false;
	}

	/********************************************************
	 Function: add
	 *******************************************************/
	void add(final int i) {
		this.m_set.set(i);
	}

	/********************************************************
	 Function: addncase
	 *******************************************************/
	void addncase // add, ignoring case.
	(final char c) {
		/* Do this in a Unicode-friendly way. */
		/* (note that duplicate adds have no effect) */
		this.add(c);
		this.add(Character.toLowerCase(c));
		this.add(Character.toTitleCase(c));
		this.add(Character.toUpperCase(c));
	}

	/********************************************************
	 Function: complement
	 *******************************************************/
	void complement() {
		this.m_complement = true;
	}

	/********************************************************
	 Function: contains
	 *******************************************************/
	boolean contains(final int i) {
		boolean result;

		result = this.m_set.get(i);

		if (this.m_complement) {
			return false == result;
		}

		return result;
	}

	/** Map set using character classes [CSA] */
	void map(final CSet set, final int[] mapping) {
		this.m_complement = set.m_complement;
		this.m_set.clearAll();
		for (final Enumeration e = set.m_set.elements(); e.hasMoreElements();) {
			final int old_value = ((Integer) e.nextElement()).intValue();
			if (old_value < mapping.length) {
				this.m_set.set(mapping[old_value]);
			}
		}
	}

	/********************************************************
	 Function: mimic
	 *******************************************************/
	void mimic(final CSet set) {
		this.m_complement = set.m_complement;
		this.m_set = (SparseBitSet) set.m_set.clone();
	}
}

/**
 * Extract character classes from NFA and simplify.
 * @author C. Scott Ananian 25-Jul-1999
 */
class CSimplifyNfa {
	private int[] ccls; // character class mapping.
	private int original_charset_size; // original charset size
	private int mapped_charset_size; // reduced charset size

	/** Compute minimum set of character classes needed to disambiguate
	 *  edges.  We optimistically assume that every character belongs to
	 *  a single character class, and then incrementally split classes
	 *  as we see edges that require discrimination between characters in
	 *  the class. [CSA, 25-Jul-1999] */
	private void computeClasses(final CSpec m_spec) {
		this.original_charset_size = m_spec.m_dtrans_ncols;
		this.ccls = new int[this.original_charset_size]; // initially all zero.

		int nextcls = 1;
		final SparseBitSet clsA = new SparseBitSet(), clsB = new SparseBitSet();
		final Hashtable h = new Hashtable();

		System.out.print("Working on character classes.");
		for (final Enumeration e = m_spec.m_nfa_states.elements(); e
			.hasMoreElements();) {
			final CNfa nfa = (CNfa) e.nextElement();
			if (nfa.m_edge == CNfa.EMPTY || nfa.m_edge == CNfa.EPSILON) {
				continue; // no discriminatory information.
			}
			clsA.clearAll();
			clsB.clearAll();
			for (int i = 0; i < this.ccls.length; i++) {
				if (nfa.m_edge == i || // edge labeled with a character
						nfa.m_edge == CNfa.CCL && nfa.m_set.contains(i)) {
					clsA.set(this.ccls[i]);
				}
				else {
					clsB.set(this.ccls[i]);
				}
			}
			// now figure out which character classes we need to split.
			clsA.and(clsB); // split the classes which show up on both sides of edge
			System.out.print(clsA.size() == 0 ? "." : ":");
			if (clsA.size() == 0) {
				continue; // nothing to do.
			}
			// and split them.
			h.clear(); // h will map old to new class name
			for (int i = 0; i < this.ccls.length; i++) {
				if (clsA.get(this.ccls[i])) {
					if (nfa.m_edge == i || nfa.m_edge == CNfa.CCL
							&& nfa.m_set.contains(i)) { // on A side
						final Integer split = new Integer(this.ccls[i]);
						if (!h.containsKey(split)) {
							h.put(split, new Integer(nextcls++)); // make new class
						}
						this.ccls[i] = ((Integer) h.get(split)).intValue();
					}
				}
			}
		}
		System.out.println();
		System.out.println("NFA has " + nextcls
				+ " distinct character classes.");

		this.mapped_charset_size = nextcls;
	}
	void simplify(final CSpec m_spec) {
		this.computeClasses(m_spec); // initialize fields.

		// now rewrite the NFA using our character class mapping.
		for (final Enumeration e = m_spec.m_nfa_states.elements(); e
			.hasMoreElements();) {
			final CNfa nfa = (CNfa) e.nextElement();
			if (nfa.m_edge == CNfa.EMPTY || nfa.m_edge == CNfa.EPSILON) {
				continue; // no change.
			}
			if (nfa.m_edge == CNfa.CCL) {
				final CSet ncset = new CSet();
				ncset.map(nfa.m_set, this.ccls); // map it.
				nfa.m_set = ncset;
			}
			else { // single character
				nfa.m_edge = this.ccls[nfa.m_edge]; // map it.
			}
		}

		// now update m_spec with the mapping.
		m_spec.m_ccls_map = this.ccls;
		m_spec.m_dtrans_ncols = this.mapped_charset_size;
	}
}

/***************************************************************
 Class: CSpec
 **************************************************************/
class CSpec {
	/***************************************************************
	 Member Variables
	 **************************************************************/

	/* Lexical States. */
	Hashtable m_states; /* Hashtable taking state indices (Integer) 
						to state name (String). */

	/* Regular Expression Macros. */
	Hashtable m_macros; /* Hashtable taking macro name (String)
						to corresponding char buffer that
						holds macro definition. */

	/* NFA Machine. */
	CNfa m_nfa_start; /* Start state of NFA machine. */
	Vector m_nfa_states; /* Vector of states, with index
							corresponding to label. */

	Vector m_state_rules[]; /* An array of Vectors of Integers.
							The ith Vector represents the lexical state
							with index i.  The contents of the ith 
							Vector are the indices of the NFA start
							states that can be matched while in
							the ith lexical state. */

	int m_state_dtrans[];

	/* DFA Machine. */
	Vector m_dfa_states; /* Vector of states, with index
							corresponding to label. */
	Hashtable m_dfa_sets; /* Hashtable taking set of NFA states
							to corresponding DFA state, 
							if the latter exists. */

	/* Accept States and Corresponding Anchors. */
	Vector m_accept_vector;
	int m_anchor_array[];

	/* Transition Table. */
	Vector m_dtrans_vector;
	int m_dtrans_ncols;
	int m_row_map[];
	int m_col_map[];

	/* Special pseudo-characters for beginning-of-line and end-of-file. */
	static final int NUM_PSEUDO = 2;
	int BOL; // beginning-of-line
	int EOF; // end-of-line

	/** NFA character class minimization map. */
	int m_ccls_map[];

	/* Regular expression token variables. */
	int m_current_token;
	char m_lexeme;
	boolean m_in_quote;
	boolean m_in_ccl;

	/* Verbose execution flag. */
	boolean m_verbose;

	/* JLex directives flags. */
	boolean m_integer_type;
	boolean m_intwrap_type;
	boolean m_yyeof;
	boolean m_count_chars;
	boolean m_count_lines;
	boolean m_cup_compatible;
	boolean m_unix;
	boolean m_public;
	boolean m_ignorecase;

	char m_init_code[];
	int m_init_read;

	char m_init_throw_code[];
	int m_init_throw_read;

	char m_class_code[];
	int m_class_read;

	char m_eof_code[];
	int m_eof_read;

	char m_eof_value_code[];
	int m_eof_value_read;

	char m_eof_throw_code[];
	int m_eof_throw_read;

	char m_yylex_throw_code[];
	int m_yylex_throw_read;

	/* Class, function, type names. */
	char m_class_name[] = { 'Y', 'y', 'l', 'e', 'x' };
	char m_implements_name[] = {};
	char m_function_name[] = { 'y', 'y', 'l', 'e', 'x' };
	char m_type_name[] = { 'Y', 'y', 't', 'o', 'k', 'e', 'n' };

	/***************************************************************
	 Constants
	 ***********************************************************/
	static final int NONE = 0;
	static final int START = 1;
	static final int END = 2;

	/***************************************************************
	 Function: CSpec
	 Description: Constructor.
	 **************************************************************/
	CSpec(final CLexGen lexGen) {
		/* Initialize regular expression token variables. */
		this.m_current_token = CLexGen.EOS;
		this.m_lexeme = '\0';
		this.m_in_quote = false;
		this.m_in_ccl = false;

		/* Initialize hashtable for lexer states. */
		this.m_states = new Hashtable();
		this.m_states.put(
			new String("YYINITIAL"),
			new Integer(this.m_states.size()));

		/* Initialize hashtable for lexical macros. */
		this.m_macros = new Hashtable();

		/* Initialize variables for lexer options. */
		this.m_integer_type = false;
		this.m_intwrap_type = false;
		this.m_count_lines = false;
		this.m_count_chars = false;
		this.m_cup_compatible = false;
		this.m_unix = true;
		this.m_public = false;
		this.m_yyeof = false;
		this.m_ignorecase = false;

		/* Initialize variables for JLex runtime options. */
		this.m_verbose = true;

		this.m_nfa_start = null;
		this.m_nfa_states = new Vector();

		this.m_dfa_states = new Vector();
		this.m_dfa_sets = new Hashtable();

		this.m_dtrans_vector = new Vector();
		this.m_dtrans_ncols = CUtility.MAX_SEVEN_BIT + 1;
		this.m_row_map = null;
		this.m_col_map = null;

		this.m_accept_vector = null;
		this.m_anchor_array = null;

		this.m_init_code = null;
		this.m_init_read = 0;

		this.m_init_throw_code = null;
		this.m_init_throw_read = 0;

		this.m_yylex_throw_code = null;
		this.m_yylex_throw_read = 0;

		this.m_class_code = null;
		this.m_class_read = 0;

		this.m_eof_code = null;
		this.m_eof_read = 0;

		this.m_eof_value_code = null;
		this.m_eof_value_read = 0;

		this.m_eof_throw_code = null;
		this.m_eof_throw_read = 0;

		this.m_state_dtrans = null;

		this.m_state_rules = null;
	}
}

/********************************************************
 Class: Utility
 *******************************************************/
class CUtility {
	/********************************************************
	 Constants
	 *******************************************************/
	static final boolean DEBUG = true;
	static final boolean SLOW_DEBUG = true;
	static final boolean DUMP_DEBUG = true;
	/*static final boolean DEBUG = false;
	 static final boolean SLOW_DEBUG = false;
	 static final boolean DUMP_DEBUG = false;*/
	static final boolean DESCENT_DEBUG = false;
	static final boolean OLD_DEBUG = false;
	static final boolean OLD_DUMP_DEBUG = false;
	static final boolean FOODEBUG = false;
	static final boolean DO_DEBUG = false;

	/********************************************************
	 Constants: Integer Bounds
	 *******************************************************/
	static final int INT_MAX = 2147483647;

	static final int MAX_SEVEN_BIT = 127;
	static final int MAX_EIGHT_BIT = 255;
	static final int MAX_SIXTEEN_BIT = 65535;

	/********************************************************
	 Function: ASSERT
	 Description: Debugging routine.
	 *******************************************************/
	static void ASSERT(final boolean expr) {
		if (CUtility.DEBUG && false == expr) {
			System.out.println("Assertion Failed");
			throw new Error("Assertion Failed.");
		}
	}

	/********************************************************
	 Function: bytencmp
	 Description: Compares up to n elements of 
	 byte array a[] against byte array b[].
	 The first byte comparison is made between 
	 a[a_first] and b[b_first].  Comparisons continue
	 until the null terminating byte '\0' is reached
	 or until n bytes are compared.
	 Return Value: Returns 0 if arrays are the 
	 same up to and including the null terminating byte 
	 or up to and including the first n bytes,
	 whichever comes first.
	 *******************************************************/
	static int bytencmp(
		final byte a[],
		final int a_first,
		final byte b[],
		final int b_first,
		final int n) {
		int elem;

		for (elem = 0; elem < n; ++elem) {
			/*System.out.print((char) a[a_first + elem]);
			 System.out.print((char) b[b_first + elem]);*/

			if ('\0' == a[a_first + elem] && '\0' == b[b_first + elem]) {
				/*System.out.println("return 0");*/
				return 0;
			}
			if (a[a_first + elem] < b[b_first + elem]) {
				/*System.out.println("return 1");*/
				return 1;
			}
			else if (a[a_first + elem] > b[b_first + elem]) {
				/*System.out.println("return -1");*/
				return -1;
			}
		}

		/*System.out.println("return 0");*/
		return 0;
	}

	/********************************************************
	 Function: charncmp
	 *******************************************************/
	static int charncmp(
		final char a[],
		final int a_first,
		final char b[],
		final int b_first,
		final int n) {
		int elem;

		for (elem = 0; elem < n; ++elem) {
			if ('\0' == a[a_first + elem] && '\0' == b[b_first + elem]) {
				return 0;
			}
			if (a[a_first + elem] < b[b_first + elem]) {
				return 1;
			}
			else if (a[a_first + elem] > b[b_first + elem]) {
				return -1;
			}
		}

		return 0;
	}

	/***************************************************************
	 Function: doubleSize
	 **************************************************************/
	static byte[] doubleSize(final byte oldBuffer[]) {
		final byte newBuffer[] = new byte[2 * oldBuffer.length];
		int elem;

		for (elem = 0; elem < oldBuffer.length; ++elem) {
			newBuffer[elem] = oldBuffer[elem];
		}

		return newBuffer;
	}

	/***************************************************************
	 Function: doubleSize
	 **************************************************************/
	static char[] doubleSize(final char oldBuffer[]) {
		final char newBuffer[] = new char[2 * oldBuffer.length];
		int elem;

		for (elem = 0; elem < oldBuffer.length; ++elem) {
			newBuffer[elem] = oldBuffer[elem];
		}

		return newBuffer;
	}

	/********************************************************
	 Function: enter
	 Description: Debugging routine.
	 *******************************************************/
	static void enter(final String descent, final char lexeme, final int token) {
		System.out.println("Entering " + descent + " [lexeme: " + lexeme
				+ "] [token: " + token + "]");
	}

	/********************************************************
	 Function: hex2bin
	 *******************************************************/
	static char hex2bin(final char c) {
		if ('0' <= c && '9' >= c) {
			return (char) (c - '0');
		}
		else if ('a' <= c && 'f' >= c) {
			return (char) (c - 'a' + 10);
		}
		else if ('A' <= c && 'F' >= c) {
			return (char) (c - 'A' + 10);
		}

		CError.impos("Bad hexidecimal digit" + c);
		return 0;
	}

	/********************************************************
	 Function: ishexdigit
	 *******************************************************/
	static boolean ishexdigit(final char c) {
		if ('0' <= c && '9' >= c || 'a' <= c && 'f' >= c || 'A' <= c
				&& 'F' >= c) {
			return true;
		}

		return false;
	}

	/********************************************************
	 Function: isnewline
	 *******************************************************/
	static boolean isnewline(final char c) {
		if ('\n' == c || '\r' == c) {
			return true;
		}

		return false;
	}

	/********************************************************
	 Function: isoctdigit
	 *******************************************************/
	static boolean isoctdigit(final char c) {
		if ('0' <= c && '7' >= c) {
			return true;
		}

		return false;
	}

	/********************************************************
	 Function: isspace
	 *******************************************************/
	static boolean isspace(final char c) {
		if ('\b' == c || '\t' == c || '\n' == c || '\f' == c || '\r' == c
				|| ' ' == c) {
			return true;
		}

		return false;
	}

	/********************************************************
	 Function: leave
	 Description: Debugging routine.
	 *******************************************************/
	static void leave(final String descent, final char lexeme, final int token) {
		System.out.println("Leaving " + descent + " [lexeme:" + lexeme
				+ "] [token:" + token + "]");
	}

	/********************************************************
	 Function: oct2bin
	 *******************************************************/
	static char oct2bin(final char c) {
		if ('0' <= c && '7' >= c) {
			return (char) (c - '0');
		}

		CError.impos("Bad octal digit " + c);
		return 0;
	}
}

/***************************************************************
 Class: Main
 Description: Top-level lexical analyzer generator function.
 **************************************************************/
public class JLex {
	/***************************************************************
	 Function: main
	 **************************************************************/
	public static void main(final String arg[]) throws java.io.IOException {
		CLexGen lg;

		if (arg.length < 1) {
			System.out.println("Usage: JLex.Main <filename>");
			return;
		}

		/* Note: For debuging, it may be helpful to remove the try/catch
		 block and permit the Exception to propagate to the top level. 
		 This gives more information. */
		try {
			lg = new CLexGen(arg[0]);
			lg.generate();
		}
		catch (final Error e) {
			System.out.println(e.getMessage());
		}
	}
}

/*
 * SparseBitSet 25-Jul-1999.
 * C. Scott Ananian <cananian@alumni.princeton.edu>
 *
 * Re-implementation of the standard java.util.BitSet to support sparse
 * sets, which we need to efficiently support unicode character classes.
 */

/**
 * A set of bits. The set automatically grows as more bits are
 * needed. 
 *
 * @version 	1.00, 25 Jul 1999
 * @author C. Scott Ananian
 */
final class SparseBitSet implements Cloneable {
	// BINARY OPERATION MACHINERY
	private static interface BinOp {
		public long op(long a, long b);
	}
	/** Sorted array of bit-block offsets. */
	int offs[];
	/** Array of bit-blocks; each holding BITS bits. */
	long bits[];
	/** Number of blocks currently in use. */
	int size;
	/** log base 2 of BITS, for the identity: x/BITS == x >> LG_BITS */
	static final private int LG_BITS = 6;
	/** Number of bits in a block. */
	static final private int BITS = 1 << SparseBitSet.LG_BITS;

	/** BITS-1, using the identity: x % BITS == x & (BITS-1) */
	static final private int BITS_M1 = SparseBitSet.BITS - 1;

	private static final BinOp AND = new BinOp() {
		public final long op(final long a, final long b) {
			return a & b;
		}
	};

	private static final BinOp OR = new BinOp() {
		public final long op(final long a, final long b) {
			return a | b;
		}
	};

	private static final BinOp XOR = new BinOp() {
		public final long op(final long a, final long b) {
			return a ^ b;
		}
	};
	private static final void binop(
		final SparseBitSet a,
		final SparseBitSet b,
		final BinOp op) {
		int nsize = a.size + b.size;
		long[] nbits;
		int[] noffs;
		int a_zero, a_size;
		// be very clever and avoid allocating more memory if we can.
		if (a.bits.length < nsize) { // oh well, have to make working space.
			nbits = new long[nsize];
			noffs = new int[nsize];
			a_zero = 0;
			a_size = a.size;
		}
		else { // reduce, reuse, recycle!
			nbits = a.bits;
			noffs = a.offs;
			a_zero = a.bits.length - a.size;
			a_size = a.bits.length;
			System.arraycopy(a.bits, 0, a.bits, a_zero, a.size);
			System.arraycopy(a.offs, 0, a.offs, a_zero, a.size);
		}
		// ok, crunch through and binop those sets!
		nsize = 0;
		for (int i = a_zero, j = 0; i < a_size || j < b.size;) {
			long nb;
			int no;
			if (i < a_size && (j >= b.size || a.offs[i] < b.offs[j])) {
				nb = op.op(a.bits[i], 0);
				no = a.offs[i];
				i++;
			}
			else if (j < b.size && (i >= a_size || a.offs[i] > b.offs[j])) {
				nb = op.op(0, b.bits[j]);
				no = b.offs[j];
				j++;
			}
			else { // equal keys; merge.
				nb = op.op(a.bits[i], b.bits[j]);
				no = a.offs[i];
				i++;
				j++;
			}
			if (nb != 0) {
				nbits[nsize] = nb;
				noffs[nsize] = no;
				nsize++;
			}
		}
		a.bits = nbits;
		a.offs = noffs;
		a.size = nsize;
	}
	/**
	 * Compares two SparseBitSets for equality.
	 * @return true if the objects are the same; false otherwise.
	 */
	public static boolean equals(final SparseBitSet a, final SparseBitSet b) {
		for (int i = 0, j = 0; i < a.size || j < b.size;) {
			if (i < a.size && (j >= b.size || a.offs[i] < b.offs[j])) {
				if (a.bits[i++] != 0) {
					return false;
				}
			}
			else if (j < b.size && (i >= a.size || a.offs[i] > b.offs[j])) {
				if (b.bits[j++] != 0) {
					return false;
				}
			}
			else { // equal keys
				if (a.bits[i++] != b.bits[j++]) {
					return false;
				}
			}
		}
		return true;
	}
	/** Self-test. */
	public static void main(final String[] args) {
		final int ITER = 500;
		final int RANGE = 65536;
		final SparseBitSet a = new SparseBitSet();
		CUtility.ASSERT(!a.get(0) && !a.get(1));
		CUtility.ASSERT(!a.get(123329));
		a.set(0);
		CUtility.ASSERT(a.get(0) && !a.get(1));
		a.set(1);
		CUtility.ASSERT(a.get(0) && a.get(1));
		a.clearAll();
		CUtility.ASSERT(!a.get(0) && !a.get(1));
		final java.util.Random r = new java.util.Random();
		final java.util.Vector v = new java.util.Vector();
		for (int n = 0; n < ITER; n++) {
			final int rr = (r.nextInt() >>> 1) % RANGE << 1;
			a.set(rr);
			v.addElement(new Integer(rr));
			// check that all the numbers are there.
			CUtility.ASSERT(a.get(rr) && !a.get(rr + 1) && !a.get(rr - 1));
			for (int i = 0; i < v.size(); i++) {
				CUtility.ASSERT(a.get(((Integer) v.elementAt(i)).intValue()));
			}
		}
		final SparseBitSet b = (SparseBitSet) a.clone();
		CUtility.ASSERT(a.equals(b) && b.equals(a));
		for (int n = 0; n < ITER / 2; n++) {
			final int rr = (r.nextInt() >>> 1) % v.size();
			final int m = ((Integer) v.elementAt(rr)).intValue();
			b.clear(m);
			v.removeElementAt(rr);
			// check that numbers are removed properly.
			CUtility.ASSERT(!b.get(m));
		}
		CUtility.ASSERT(!a.equals(b));
		SparseBitSet c = (SparseBitSet) a.clone();
		final SparseBitSet d = (SparseBitSet) a.clone();
		c.and(a);
		CUtility.ASSERT(c.equals(a) && a.equals(c));
		c.xor(a);
		CUtility.ASSERT(!c.equals(a) && c.size() == 0);
		d.or(b);
		CUtility.ASSERT(d.equals(a) && !b.equals(d));
		d.and(b);
		CUtility.ASSERT(!d.equals(a) && b.equals(d));
		d.xor(a);
		CUtility.ASSERT(!d.equals(a) && !b.equals(d));
		c.or(d);
		c.or(b);
		CUtility.ASSERT(c.equals(a) && a.equals(c));
		c = (SparseBitSet) d.clone();
		c.and(b);
		CUtility.ASSERT(c.size() == 0);
		System.out.println("Success.");
	}

	/**
	 * Creates an empty set.
	 */
	public SparseBitSet() {
		this.bits = new long[4];
		this.offs = new int[4];
		this.size = 0;
	}

	/**
	 * Creates an empty set with the specified size.
	 * @param nbits the size of the set
	 */
	public SparseBitSet(final int nbits) {
		this();
	}

	/**
	 * Creates an empty set with the same size as the given set.
	 */
	public SparseBitSet(final SparseBitSet set) {
		this.bits = new long[set.size];
		this.offs = new int[set.size];
		this.size = 0;
	}

	/**
	 * Logically ANDs this bit set with the specified set of bits.
	 * @param set the bit set to be ANDed with
	 */
	public void and(final SparseBitSet set) {
		SparseBitSet.binop(this, set, SparseBitSet.AND);
	}

	private int bsearch(final int bnum) {
		int l = 0, r = this.size; // search interval is [l, r)
		while (l < r) {
			final int p = (l + r) / 2;
			if (bnum < this.offs[p]) {
				r = p;
			}
			else if (bnum > this.offs[p]) {
				l = p + 1;
			}
			else {
				return p;
			}
		}
		CUtility.ASSERT(l == r);
		return l; // index at which the bnum *should* be, if it's not.
	}

	/**
	 * Clears a bit.
	 * @param bit the bit to be cleared
	 */
	public void clear(final int bit) {
		final int bnum = bit >> SparseBitSet.LG_BITS;
		final int idx = this.bsearch(bnum);
		if (idx >= this.size || this.offs[idx] != bnum) {
			this.new_block(idx, bnum);
		}
		this.bits[idx] &= ~(1L << (bit & SparseBitSet.BITS_M1));
	}

	/**
	 * Clears all bits.
	 */
	public void clearAll() {
		this.size = 0;
	}

	/**
	 * Clones the SparseBitSet.
	 */
	public Object clone() {
		try {
			final SparseBitSet set = (SparseBitSet) super.clone();
			set.bits = (long[]) this.bits.clone();
			set.offs = (int[]) this.offs.clone();
			return set;
		}
		catch (final CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}
	/**
	 * Return an <code>Enumeration</code> of <code>Integer</code>s
	 * which represent set bit indices in this SparseBitSet.
	 */
	public Enumeration elements() {
		return new Enumeration() {
			int idx = -1, bit = SparseBitSet.BITS;
			{
				this.advance();
			}
			private void advance() {
				while (this.idx < SparseBitSet.this.size) {
					while (++this.bit < SparseBitSet.BITS) {
						if (0 != (SparseBitSet.this.bits[this.idx] & 1L << this.bit)) {
							return;
						}
					}
					this.idx++;
					this.bit = -1;
				}
			}
			public boolean hasMoreElements() {
				return this.idx < SparseBitSet.this.size;
			}
			public Object nextElement() {
				final int r =
					this.bit
							+ (SparseBitSet.this.offs[this.idx] << SparseBitSet.LG_BITS);
				this.advance();
				return new Integer(r);
			}
		};
	}
	/**
	 * Compares this object against the specified object.
	 * @param obj the object to commpare with
	 * @return true if the objects are the same; false otherwise.
	 */
	public boolean equals(final Object obj) {
		if (obj != null && obj instanceof SparseBitSet) {
			return SparseBitSet.equals(this, (SparseBitSet) obj);
		}
		return false;
	}
	/**
	 * Gets a bit.
	 * @param bit the bit to be gotten
	 */
	public boolean get(final int bit) {
		final int bnum = bit >> SparseBitSet.LG_BITS;
		final int idx = this.bsearch(bnum);
		if (idx >= this.size || this.offs[idx] != bnum) {
			return false;
		}
		return 0 != (this.bits[idx] & 1L << (bit & SparseBitSet.BITS_M1));
	}
	/**
	 * Gets the hashcode.
	 */
	public int hashCode() {
		long h = 1234;
		for (int i = 0; i < this.size; i++) {
			h ^= this.bits[i] * this.offs[i];
		}
		return (int) (h >> 32 ^ h);
	}

	private void insert_block(final int idx, final int bnum) {
		CUtility.ASSERT(idx <= this.size);
		CUtility.ASSERT(idx == this.size || this.offs[idx] != bnum);
		System.arraycopy(this.bits, idx, this.bits, idx + 1, this.size - idx);
		System.arraycopy(this.offs, idx, this.offs, idx + 1, this.size - idx);
		this.offs[idx] = bnum;
		this.bits[idx] = 0; //clear them bits.
		this.size++;
	}

	/** Check validity. */
	private boolean isValid() {
		if (this.bits.length != this.offs.length) {
			return false;
		}
		if (this.size > this.bits.length) {
			return false;
		}
		if (this.size != 0 && 0 <= this.offs[0]) {
			return false;
		}
		for (int i = 1; i < this.size; i++) {
			if (this.offs[i] < this.offs[i - 1]) {
				return false;
			}
		}
		return true;
	}

	private void new_block(final int bnum) {
		this.new_block(this.bsearch(bnum), bnum);
	}
	private void new_block(final int idx, final int bnum) {
		if (this.size == this.bits.length) { // resize
			final long[] nbits = new long[this.size * 3];
			final int[] noffs = new int[this.size * 3];
			System.arraycopy(this.bits, 0, nbits, 0, this.size);
			System.arraycopy(this.offs, 0, noffs, 0, this.size);
			this.bits = nbits;
			this.offs = noffs;
		}
		CUtility.ASSERT(this.size < this.bits.length);
		this.insert_block(idx, bnum);
	}

	/**
	 * Logically ORs this bit set with the specified set of bits.
	 * @param set the bit set to be ORed with
	 */
	public void or(final SparseBitSet set) {
		SparseBitSet.binop(this, set, SparseBitSet.OR);
	}

	/**
	 * Sets a bit.
	 * @param bit the bit to be set
	 */
	public void set(final int bit) {
		final int bnum = bit >> SparseBitSet.LG_BITS;
		final int idx = this.bsearch(bnum);
		if (idx >= this.size || this.offs[idx] != bnum) {
			this.new_block(idx, bnum);
		}
		this.bits[idx] |= 1L << (bit & SparseBitSet.BITS_M1);
	}
	/**
	 * Calculates and returns the set's size
	 */
	public int size() {
		return this.size == 0 ? 0
				: 1 + this.offs[this.size - 1] << SparseBitSet.LG_BITS;
	}

	/**
	 * Converts the SparseBitSet to a String.
	 */
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append('{');
		for (final Enumeration e = this.elements(); e.hasMoreElements();) {
			if (sb.length() > 1) {
				sb.append(", ");
			}
			sb.append(e.nextElement());
		}
		sb.append('}');
		return sb.toString();
	}
	/**
	 * Logically XORs this bit set with the specified set of bits.
	 * @param set the bit set to be XORed with
	 */
	public void xor(final SparseBitSet set) {
		SparseBitSet.binop(this, set, SparseBitSet.XOR);
	}
}

/************************************************************************
 JLEX COPYRIGHT NOTICE, LICENSE AND DISCLAIMER.
 
 Copyright 1996 by Elliot Joel Berk
 
 Permission to use, copy, modify, and distribute this software and its
 documentation for any purpose and without fee is hereby granted,
 provided that the above copyright notice appear in all copies and that
 both the copyright notice and this permission notice and warranty
 disclaimer appear in supporting documentation, and that the name of
 Elliot Joel Berk not be used in advertising or publicity pertaining 
 to distribution of the software without specific, written prior permission.
 
 Elliot Joel Berk disclaims all warranties with regard to this software, 
 including all implied warranties of merchantability and fitness.  In no event
 shall Elliot Joel Berk be liable for any special, indirect or consequential
 damages or any damages whatsoever resulting from loss of use, data or
 profits, whether in an action of contract, negligence or other
 tortious action, arising out of or in connection with the use or
 performance of this software.
 ***********************************************************************/
// set emacs indentation
// Local Variables:
// c-basic-offset:2
// End:
