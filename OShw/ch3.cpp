#include<iostream>
#include<cstring>
#include<string>
#include <unistd.h>
#include<stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include<list>
using namespace std;

list<string> his;

void creatProcess(char *args[], bool flag){
  pid_t proc = fork();
  if(proc<0){
    cout<<"Unable to fork"<<endl<<endl;
    exit(0);
  }else if(proc==0){
    if(args[0]=="history"){
      for(int i=his.size()-1;i>=0;i--){
        cout<<i<<" "<<his[i]<<endl;
      }
      cout<<endl;
    }
    else if(execvp(args[0], args)==-1){
      cout<<"Unable to load the executable "<<args[0]<<endl<<endl;
      exit(0);
    }
    exit(0);
  }else if(!flag){
    //int status;
    //wait(&status);
    waitpid(proc,NULL,0);
    cout<<"complete"<<endl;
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
    bool flag = false;
    for(int i=0;i<=s.length();i++){
      if(i==s.length()||s[i]==' '){
        args[count] = new char[temp.length()+1];
        strcpy(args[count], temp.c_str());
        count++;
        temp = "";
      }else if(i==s.length()-1&&s[i]=='&'){
        flag=true;
        break;
      }
      else{
        temp+=s[i];
      }
    }
    if(args[0]!=NULL){
      his.push_back(s);
      if(his.size()>10){
        his.pop_front();
      }
      creatProcess(args, flag);
    }
  }
}
