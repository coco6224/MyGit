#include<iostream>
#include<thread>
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
  Avr = -1;
  Min = -1;
  Max = -1;

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
