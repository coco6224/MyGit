#include<iostream>
#include<string>
#include<vector>
#include<sstream>
#include<thread>

using namespace std;

void avrage(vector<int>nums, int &output) {
	int temp = 0;
	for (vector<int>::iterator it = nums.begin(); it != nums.end();it++) {
		temp += *it;
	}
	output = temp / nums.size();
}

void minimum(vector<int>nums, int &output) {
	int temp = 999999999;
	for (vector<int>::iterator it = nums.begin(); it != nums.end();it++) {
		if (*it < temp) {
			temp = *it;
		}
	}
	output = temp;
}

void maximum(vector<int>nums, int &output) {
	int temp = -9999999999;
	for (vector<int>::iterator it = nums.begin(); it != nums.end();it++) {
		if (*it > temp) {
			temp = *it;
		}
	}
	output = temp;
}

int main()
{
	while (true) {
		string s;
		getline(cin, s);
		stringstream ss;
		int num;
		vector<int>nums;
		while (ss >> num) {
			nums.push_back(num);
		}
		if (nums.size() > 0) {
			int avr;
			int max;
			int min;
			thread avra(avrage, nums, avr);
			thread mini(minimum, nums, min);
			thread maxi(maximum, nums, max);
			avra.join();
			mini.join();
			maxi.join();
			cout << "The avrage value is " << avr << endl;
			cout << "The minimum value is " << min << endl;
			cout << "The maximum value is " << max << endl;
		}
	}
    return 0;
}

