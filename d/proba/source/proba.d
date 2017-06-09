import std.stdio;

struct S {
    int i, j;
};

int[S] a;

void main()
{
    a[S(1,2)] = 3;
    a[S(4,5)] = 6;
    writeln(a[S(4,5)]);
}
