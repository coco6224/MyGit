#include<iostream>
#include<vector>
#include<thread>
using namespace std;

vector<int>fib;

void generate() {
	if (fib.size() == 0) {
		fib.push_back(0);
	}
	else if (fib.size() == 1) {
		fib.push_back(1);
	}
	else {
		fib.push_back(fib[fib.size() - 1] + fib[fib.size() - 2]);
	}
}

int main()
{
	while (true) {
		int num;
		cin >> num;
		cout << endl;
		for (int i = 0;i < num;i++) {
			thread g(generate);
			g.join();
		}
		for (int i = 0;i < fib.size();i++) {
			cout << fib[i] << endl;
		}
		cout << endl;
	}
    return 0;
}

