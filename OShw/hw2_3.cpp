#include<iostream>
#include<string>
using namespace std;

int main(){
  while(true){
    cout<<"osh> ";
    string s;
    getline(cin, s);
    char *args[81];
    string temp = "";
    int count = 0;
    for(int i=0;i<s.length();i++){
      if(s[i]!=' '){
        temp+=s[i];
      }else{
        args[count] = new char[temp.length()+1];
        strcpy(args[count], temp.c_str());
        count++;
        temp = "";
      }
    }
    cout<<args[0]<<endl;
  }
}
