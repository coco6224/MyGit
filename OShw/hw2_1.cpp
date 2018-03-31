#include<iostream>
#include<vector>
#include<sstream>
#include<thread>
using namespace std;

class sta{
public:
  sta(){}
  void average(vector<int> input){
    int output = 0;
    for(vector<int>::iterator it = input.begin(); it!=input.end();it++){
      output+=*it;
    }
    avr = (output/input.size());
  }

  void Max(vector<int> input){
    int output = -9999999999;
    for(vector<int>::iterator it = input.begin(); it!=input.end();it++){
      if(*it>output){
        output = *it;
      }
    }
    MAX = output;
  }
  void Min(vector<int> input){
    int output = 9999999999;
    for(vector<int>::iterator it = input.begin(); it!=input.end();it++){
      if(*it<output){
        output = *it;
      }
    }
    MIN = output;
  }
  int avr, MAX, MIN;
};
int main(){
  while(true){
    string in;
    cin>>in;
    vector<int> nums;
    stringstream ss(in);
    int num;
    while(ss>>num){
      nums.push_back(num);
    }

    sta stas;
    thread avr(stas.average(nums));
    thread MAX(stas.Max(nums));
    thread MIN(stas.Min(nums));

    cout<<"The average value is "<<stas.avr<<endl;
    cout<<"The minimum value is "<<stas.MIN<<endl;
    cout<<"The maximum value is "<<stas.MAX<<endl;
  }
  return 0;
}
