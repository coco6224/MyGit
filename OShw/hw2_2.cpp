#include<iostream>  
#include<vector> 
#include<thread> 
using namespace std; 
  
vector<int>fib; 
  
void generate(int n) {
  if(fib.size()<n-1){
    generate(n-1);
  }else{
    fib.push_back(fib[n-1]+fib[n-2]);
  }
  /*
	if (fib.size() == 0) { 
   fib.push_back(0);  
 	 }  
 	 else if (fib.size() == 1){  
 	 	 fib.push_back(1);  
 	 }  
 	 else{  
 	 	 fib.push_back(fib[fib.size() - 1] + fib[fib.size() - 2]);  
 	 }
  */  
 }  
  
 int main()  
 {  
 	 while (true){  
 	 	 int num;  
 	 	 cin >> num;  
 	 	 cout << endl;
    thread g(generate, ref(num));
    g.join();
 	 	 for (int i = 0;i < fib.size();i++) {  
 	 	 	 cout << fib[i] << endl; 
 	 	 }  
 	 	 cout << endl;  
 	 }  
         return 0;  
 }  
  
 
