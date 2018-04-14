#include<iostream>
#include<string>
#include<vector>
#include<sstream>
#include<thread>

using namespace std;

double avr;
double max;
double min;

void avrage(vector<int>nums) {
	int temp = 0;
	for (vector<int>::iterator it = nums.begin(); it != nums.end(); it++) {
		temp += *it;
	}
	avr = temp / nums.size();
}

void minimum(vector<int>nums) {
	int temp = 999999999;
	for (vector<int>::iterator it = nums.begin(); it != nums.end(); it++) {
		if (*it < temp) {
			temp = *it;
		}
	}
	min = temp;
}

void maximum(vector<int>nums) {
	int temp = -9999999999;
	for (vector<int>::iterator it = nums.begin(); it != nums.end(); it++) {
		if (*it > temp) {
			temp = *it;
		}
	}
	max = temp;
}

int main()
{
	while (true) {
		avr = -1;
		min = -1;
		max = -1;
		string s;
		getline(cin, s);
		stringstream ss;
		int num;
		vector<int>nums;
		ss << s;
		while (ss >> num) {
			nums.push_back(num);
		}
		if (nums.size() > 0) {
			thread avra(avrage, ref(nums), ref(avr));
			thread mini(minimum, ref(nums), ref(min));
			thread maxi(maximum, ref(nums), ref(max));
			avra.join();
			mini.join();
			maxi.join();
		}
		int count = 0;
		while(count<3){
			if(avr!=-1){
				cout << "The avrage value is " << avr << endl;
				count++;
				avr = -1;
			}

			if(min!=-1){
				cout << "The minimum value is " << min << endl;
				count++;
				avr = -1;
			}

			if(max!=-1){
				cout << "The maximum value is " << max << endl;
				count++;
				avr = -1;
			}
		}
		cout << endl;
	}
	return 0;
}
