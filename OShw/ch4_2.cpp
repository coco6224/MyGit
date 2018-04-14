#include<iostream>
#include<thread>
#include<vector>
using namespace std;

vector<int> fib;

void generate(int n){
  if(fib.size()<n-1){
    generate(n-1);
  }else{
    if(n==0){
      fib.push_back(0);
    }else if(n==1){
      fib.push_back(1);
    }else{
      fib.push_back(fib[n-2]+fib[n-3]);
    }
  }
}

int main(){
  while(true){
    int num;
    cin>>num;
    cout<<endl;
    generate(num);
    for(vector<int>::iterator it = fib.begin();it!=fib.end();it++){
      cout<<*it<<endl;
    }
    cout<<endl;
    fib.clear();
  }
}
