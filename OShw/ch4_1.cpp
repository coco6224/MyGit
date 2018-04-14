#include<iostream>
#include<thread>
#include<string>
#include<sstream>
#include<vector>
using namespace std;

int Avr;
int Min;
int Max;

void average(){
  Avr = 321;
}

void minimum(){
  Min = 456;
}

void maximum(){
  Max = 1010;
}

int main(){
  while(true){
    Avr = -1;
    Min = -1;
    Max = -1;

    vector<int> nums;
    string s;
    getline(cin,s);
    string temp = "";
    for(int i=0;i<s.length();i++){
      if(s[i]!=' '){
        temp+=s[i];
      }else{
        sstream ss;
        int num;
        ss<<temp;
        ss>>num;
        nums.push_back(num);
        temp = "";
      }
    }

    cout<<nums.size()<<endl;

    thread t(average);
    thread tt(minimum);
    thread ttt(maximum);

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
