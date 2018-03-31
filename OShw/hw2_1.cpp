#include<iostream>
#include<vector>
#include<sstream>
#include<thread>
using namespace std;

class sta{
public:
  sta(){}
  void average(){
    int output = 0;
    for(vector<int>::iterator it = input.begin(); it!=input.end();it++){
      output+=*it;
    }
    avr = (output/input.size());
  }

  void Max(){
    int output = -9999999999;
    for(vector<int>::iterator it = input.begin(); it!=input.end();it++){
      if(*it>output){
        output = *it;
      }
    }
    MAX = output;
  }
  void Min(){
    int output = 9999999999;
    for(vector<int>::iterator it = input.begin(); it!=input.end();it++){
      if(*it<output){
        output = *it;
      }
    }
    MIN = output;
  }
  int avr, MAX, MIN;
  vector<int> input;
};
int main(){
  while(true){
    string in;
    cin>>in;
    stringstream ss(in);
    int num;
    sta stas;
    while(ss>>num){
      stas.input.push_back(num);
    }

    thread avr(stas.average);
    thread MAX(stas.Max;
    thread MIN(stas.Min);

    cout<<"The average value is "<<stas.avr<<endl;
    cout<<"The minimum value is "<<stas.MIN<<endl;
    cout<<"The maximum value is "<<stas.MAX<<endl;
  }
  return 0;
}
