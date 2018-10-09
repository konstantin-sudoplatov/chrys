module crank;
import std.stdio;
import tools;

class A {}

class B: A {}

void main()
{

	A a = new B;
	scast!B(a);
}
