module tools;

/**
 *  Compile time function. Converts a string of expressions into a block of code, wich outputs those exps.
 * 
 *  Using: mixin(varList.w);
 *  Parameters: 
 *  sExpLst = list of expressions separated by commas
 *  Returns: a block of code, which outputs the expression titles and values and intended to be mixed into the source code.
*/
string w(string sExpLst) {
    import std.string, std.format;
    string[] asExp = sExpLst.split(",");
    string sRes = "import std.stdio;\n";
    foreach(s; asExp) {
        s = s.strip;
        sRes ~= format(q{write("%s: "); write(typeid(%s)); write(" = "); writeln(%s);}, s, s, s) ~ "\n";
    }

    return sRes;
}
///
unittest {
    import std.stdio;

    int i = 1;
    int j = 2;
    int* p = &j;
    assert("i, *p".w == `import std.stdio;
write("i: "); write(typeid(i)); write(" = "); writeln(i);
write("*p: "); write(typeid(*p)); write(" = "); writeln(*p);
`
        );

    assert("i+(*p)".w, `import std.stdio;
write("i+(*p): "); write(typeid(i+(*p))); write(" = "); writeln(i+(*p));`
        );
}
