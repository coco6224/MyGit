#include<iostream>
#include<sstream>
#include<semaphore.h>
#include<pthread.h>
#include<unistd.h>
#include<stdlib.h>
#include<time.h>
#include<iomanip>
#define BUFFER_SIZE 5
using namespace std;

int buffer[BUFFER_SIZE];
int PIndex;
int CIndex;
sem_t full;
sem_t empty;
sem_t mutex;
int PCount;
int CCount;

void printBuffer(){
  cout<<"[";
  for(int i=0;i<BUFFER_SIZE;i++){
    if(buffer[i]<0){
      cout<<setw(2)<<"  ";
    }
    else{
      cout<<setw(2)<<buffer[i];
    }
    if(i!=BUFFER_SIZE-1){
      cout<<"|";
    }
  }
  cout<<"]"<<endl<<endl;
}

void insert(int n, int PID){
  sem_wait(&empty); //等待有空位
  sem_wait(&mutex);

  cout<<"Producer"<<PID<<" insert "<<n<<endl;

  buffer[PIndex]=n;
  PIndex+=1;
  PIndex=PIndex%BUFFER_SIZE;

  printBuffer();

  sem_post(&full); //增加buffer內容個數
  sem_post(&mutex);
}

void remove(int CID){
  sem_wait(&full); //等buffer有東西
  sem_wait(&mutex);

  cout<<"Comsumer"<<CID<<" remove "<<buffer[CIndex]<<endl;

  buffer[CIndex]=-1;
  CIndex+=1;
  CIndex=CIndex%BUFFER_SIZE;

  printBuffer();

  sem_post(&empty); //增加buffer空位
  sem_post(&mutex);
}

void* producer(void* ptr){
  int PID=(++PCount);
  cout<<"Producer"<<PID<<" start to produce."<<endl;
  while(true){
    int n=rand()%100;
    insert(n,PID);
    sleep(rand()%5);
  }
}

void* comsumer(void* ptr){
  int CID=(++CCount);
  cout<<"Comsumer"<<CID<<" start to comsume."<<endl;
  while(true){
    remove(CID);
    sleep(rand()%5);
  }
}

int main(int argc, char *argv[]){
  srand(time(NULL));
  if(argc==4){
    int sleepTime;
    int numOfP;
    int numOfC;

    stringstream ss;
    ss.clear();
    ss.str("");
    ss.str(argv[1]);
    ss>>sleepTime;

    ss.clear();
    ss.str("");
    ss.str(argv[2]);
    ss>>numOfP;

    ss.clear();
    ss.str("");
    ss.str(argv[3]);
    ss>>numOfC;

    for(int i=0;i<BUFFER_SIZE;i++){
      buffer[i]=-1;
    }
    PIndex=0;
    CIndex=0;

    sem_init(&full,0,0);
    sem_init(&empty,0,BUFFER_SIZE);
    sem_init(&mutex,0,1);
    
    PCount=0;

    for(int i=0;i<numOfP;i++){
      pthread_t p;
      pthread_create(&p,NULL,producer,&PCount);
    }
    
    CCount=0;

    for(int i=0;i<numOfC;i++){
      pthread_t c;
      pthread_create(&c,NULL,comsumer,&CCount);
    }

    sleep(sleepTime);
    exit(EXIT_SUCCESS);

  }else{
    cout<<"Wrong num of parameter"<<endl;
    exit(EXIT_FAILURE);
  }
}