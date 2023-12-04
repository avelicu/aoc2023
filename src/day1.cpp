#include <iostream>
#include <cctype>
#include <cstdlib>

using namespace std;

int main() {
	int sum = 0;
	string line;
	while (getline(cin, line)) {
		int firstNum = -1;
		int lastNum = -1;

		for (char c : line) {
			if (isdigit(c)) {
				int num = c - '0';
				if (firstNum == -1) firstNum = num;
				lastNum = num;
			}
		}

		sum += firstNum * 10 + lastNum;
		// cout << firstNum * 10 + lastNum << endl;
	}
	cout << sum << endl;
}