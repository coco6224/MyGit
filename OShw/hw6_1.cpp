#include<iostream>
#include<stdlib.h>
#include<pthread.h>
#include<semaphore.h>
#include<time.h>
#include<unistd.h>
using namespace std;

void goToTA(void ptr);
void teaching(void ptr);

#define CHAIR 3
sem_t student;
sem_t TA;
sem_t binSem;
sem_t teach;
int count;
int waiting;
int teachingID;

int main(){
  srand(time(NULL));
  int res=0; //result for system call

  //initialize  
  count=0;
  teachingID=0;

  //initialize for semaphore
  res=sem_init(&binSem, 0, 0);
  if(res){
    cout<<"semaphore binSem initialization failed"<<endl;
    exit(EXIT_FAILURE);
  }

  res=sem_init(&TA, 0, 0);
  if(res){
    cout<<"semaphore TA initialization failed"<<endl;
    exit(EXIT_FAILURE);
  }

  res=sem_init(&student, 0, 0);
  if(res){
    cout<<"semaphore student initialization failed"<<endl;
    exit(EXIT_FAILURE);
  }  

  res=sem_init(&teach, 0, 0);
  if(res){
    cout<<"semaphore student initialization failed"<<endl;
    exit(EXIT_FAILURE);
  }  

  sem_post(&binSem);

  //creat threads
  pthread_t TeachAssis;
  res=pthread_create(&TeachAssis,NULL,teaching,NULL);

  while(true){
    pthread_t st;
    res=pthread_create(&st,NULL,goToTA, NULL);
    sleep(rand()%5);
  }

  exit(EXIT_SUCCESS);
}

void teaching(void ptr){
  while(true){
    sem_wait(&student); //if there's no student, sleep
    sem_wait(&binSem);
    waiting-=1;
    sem_post(&TA);
    sem_post(&binSem);
    sem_wait(&teach);
    sleep(rand()%10);
    cout<<teachingID<<" done. next please."<<endl;
  }
}

void goToTA(void ptr){
  sem_wait(&binSem);
  count+=1;
  int stID=count;
  cout<<stID<<" goes to TA"<<endl;
  if(waiting<CHAIR){ //still one or more chair(s)
    cout<<stID<<" sits down and waits"<<endl;
    waiting+=1;
    sem_post(&student); //wake TA up/increase student#
    sem_post(&binSem);
    sem_wait(&TA); //if TA is busy,wait
    teachingID=stID;
    cout<<stID<<" starts to be teached"<<endl;
    sem_post(&teach);
  }else{
    sem_post(&binSem);
    cout<<"There's no chair for "<<stID<<endl<<stID<<" goes away"<<endl;
  }
}