#include<iostream>
#include<cstring>
#include<string>
#include <unistd.h>
#include<stdlib.h>
using namespace std;

void creatProcess(char *args[]){
  pid_t proc = fork();
  if(proc<0){
    cout<<"Unable to fork"<<endl<<endl;
    exit();
  }else if(proc==0){
    if(execvp(args[0], args)==-1){
      cout<<"Unable to load the executable "<<args[0]<<endl<<endl;
      exit();
    }
    exit();
  }else{
    int status;
    wait(&status);
  }
}

int main(){
  while(true){
    cout<<"osh> ";
    string s;
    getline(cin, s);
    char *args[81] = {};
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
    if(args[0]!=NULL){
      creatProcess(args);
    }
  }
}
