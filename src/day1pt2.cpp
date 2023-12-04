#include <iostream>
#include <cctype>
#include <cstdlib>
#include <map>

using namespace std;

const map<string, int> numbers = {
	{ "one", 1 },
	{ "two", 2 },
	{ "three", 3 },
	{ "four", 4 },
	{ "five", 5 },
	{ "six", 6 },
	{ "seven", 7 },
	{ "eight", 8 },
	{ "nine", 9 },
};

int parse(string s, int i) {
	for (auto it = numbers.begin(); it != numbers.end(); it++) {
		if (i + it->first.length() <= s.length() && s.compare(i, it->first.length(), it->first) == 0) {
			return it->second;
		}
	}
	return -1;
}

int main() {
	int sum = 0;
	string line;
	while (getline(cin, line)) {
		// cout << line << endl;
		int firstNum = -1;
		int lastNum = -1;

		for (int i = 0; i < line.length(); i++) {
			int num = -1;
			if (isdigit(line.at(i))) num = line.at(i) - '0';
			if (num == -1) num = parse(line, i);

			if (num != -1) {
				if (firstNum == -1) firstNum = num;
				lastNum = num;
			}
		}

		sum += firstNum * 10 + lastNum;
		// cout << firstNum * 10 + lastNum << endl;
	}
	cout << sum << endl;
}