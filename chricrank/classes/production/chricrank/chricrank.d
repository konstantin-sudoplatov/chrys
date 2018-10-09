module chricrank;
import std.stdio;
import tools;

import crank.proba;

class A {}

class B: A {}

void main()
{

	A a = new B;
	scast!B(a);
}
