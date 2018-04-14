#include<iostream>
#include<thread>
#include<string>
#include<sstream>
#include<vector>
using namespace std;

int Avr;
int Min;
int Max;

void average(vector<int>nums){
  int sum = 0;
  for(vector<int>::iterator it = nums.begin();it!=nums.end();it++){
    sum+=*it;
  }
  Avr = sum/nums.size();
}

void minimum(vector<int>nums){
  int m = 9999999999;
  for(vector<int>::iterator it = nums.begin();it!=nums.end();it++){
    if(*it<m){
      m = *it;
    }
  }
  Min = m;
}

void maximum(vector<int>nums){
  int m = -9999999998;
  for(vector<int>::iterator it = nums.begin();it!=nums.end();it++){
    if(*it>m){
      m = *it;
    }
  }
  Max = m;
}

int main(){
  while(true){
    Avr = -1;
    Min = -1;
    Max = -1;

    vector<int> nums;
    string s;
    getline(cin,s);
    int num;
    stringstream ss;
    ss<<s;
    while(ss>>num){
      nums.push_back(num);
    }

    cout<<nums.size()<<endl;

    thread t(average, ref(nums));
    thread tt(minimum, ref(nums));
    thread ttt(maximum, ref(nums));

    int count = 0;
    while(count<3){
      if(Avr!=-1){
          cout<<"The average value is "<<Avr<<endl;
          Avr = -1;
          count++;
      }if(Min!=-1){
          cout<<"The minimum value is "<<Min<<endl;
          Min = -1;
          count++;
      }if(Max!=-1){
          cout<<"The maximum value is "<<Max<<endl;
          Max = -1;
          count++;
      }
    }
    cout<<endl;
    t.join();
    tt.join();
    ttt.join();
  }
}
