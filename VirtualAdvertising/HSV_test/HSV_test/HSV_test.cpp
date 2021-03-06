// HSV_test.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<iostream>
#include<opencv2\opencv.hpp>
#include<sstream>
#include<iomanip>
using namespace std;
using namespace cv;

int X = -1;
int Y = -1;

void OnMouse(int event, int x, int y, int flags, void*param) {
	if (event == CV_EVENT_LBUTTONDOWN) {
		X = x;
		Y = y;
	}
}

string intToString(int a) {
	stringstream ss;
	ss << a;
	return ss.str();
}

int main()
{
	//start from 1st image
	int index = 1;
	
	Mat ori;
	ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + intToString(index) + ").png");
	Mat white(ori.rows, ori.cols, CV_8UC3, Scalar(255, 255, 255));
	
	//check open successfully
	if (ori.empty()) {
		cout << "opening fail" << endl;
	}
	else {
		//show the 1st image
		imshow("original", ori);
		cout << "pic" << intToString(index) << endl;

		//covert color space from BGR to HSV
		Mat hsv;
		cvtColor(ori, hsv, CV_BGR2HSV);

		Mat showALL;

		//Get order from keyboard
		while (char c = waitKey(1)) {
			imshow("original", ori);

			Mat AllMask(ori.rows, ori.cols, CV_8UC3, Scalar(255, 255, 255));

			//Set mouse callback (Choose target color by mouse)
			setMouseCallback("original", OnMouse);
			
			//if the order from KB is 'a': Last img
			if (c == 'a'&&index>1) {
				showALL.release();
				Mat showALL;
				cout << endl << "pic" << intToString(index) << endl;
				index--;
				ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + intToString(index) + ").png");
				white = Mat(ori.rows, ori.cols, CV_8UC3, Scalar(255, 255, 255));
				cvtColor(ori, hsv, CV_BGR2HSV);
				destroyAllWindows();
			}
			//if the order from KB is 's': Next img
			else if (c == 's'&&index < 200) {
				showALL.release();
				Mat showALL;
				cout << endl << "pic" << intToString(index) << endl;
				index++;
				ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + intToString(index) + ").png");
				white = Mat(ori.rows, ori.cols, CV_8UC3, Scalar(255, 255, 255));
				cvtColor(ori, hsv, CV_BGR2HSV);
				destroyAllWindows();
			}

			Mat mask;
			Mat CMask;

			//if the target is choosen
			if (X >=0 && Y >=0) {
				//get target color form HSV value
				Vec3b target = hsv.at<Vec3b>(Y, X);
				int th = target.val[0];
				int ts = target.val[1];
				int tv = target.val[2];

				//Show HSV value of target
				cout << th << " " << ts << " " << tv << endl;

				//set the value range of H, S, & V to pick
				int hRange;
				int sRange;
				int vRange;
				
				if (tv <= 30) {				
					hRange = 180;			
					sRange = 255;			
					vRange = 40;				
					cout << "V<30" << endl;
				}
				else if (ts <= 55&&tv>245) {			
					hRange = 180;			
					sRange = 60;			
					vRange = 10;				
					cout << "S<55 && v>245" << endl;
				}
				else if (ts <= 40) {				
					hRange = 180;			
					sRange = 38;			
					vRange = 55;				
					cout << "S<40" << endl;
				}
				else if (ts <= 55) {				
					hRange = 180;			
					sRange = 60;			
					vRange = 90;				
					cout << "S<55" << endl;
				}
				else if (ts <= 70 && tv>150) {								
					hRange = 75;			
					sRange = 20;			
					vRange = 75;				
					cout << "S<70 && V>150" << endl;
				}
				else if (ts <= 70) {								
					hRange = 45;			
					sRange = 50;			
					vRange = 35;				
					cout << "S<70" << endl;
				}
				else if(tv<=60){								
					hRange = 10;			
					sRange = 50;			
					vRange = 30;				
					cout << "V<60" << endl;
				}
				else if (tv <= 105) {
					hRange = 15;			
					sRange = 85;			
					vRange = 20;				
					cout << "v<105" << endl;
				}
				else if (tv >= 220) {										
					hRange = 10;			
					sRange = 80;			
					vRange = 65;				
					cout << "V>220" << endl;
				}
				else{								
					hRange = 10;			
					sRange = 80;			
					vRange = 65;			
					cout << "else" << endl;
				}

				int hLB = th - hRange;
				int hUB = th + hRange+1;
				int sLB = ts - sRange;
				int sUB = ts + sRange+1;
				int vLB = tv - vRange;
				int vUB = tv + vRange+1;

				//Show U/LB
				cout << setw(4)<< hLB << " ~ " << setw(4) << hUB << endl;
				cout << setw(4) << sLB << " ~ " << setw(4) << sUB << endl;
				cout << setw(4) << vLB << " ~ " << setw(4) << vUB << endl<< endl;

				//record the position of the picked colors
				inRange(hsv, Scalar(hLB, sLB, vLB), Scalar(hUB, sUB, vUB), mask);

				//make H a circle to prevent red becoming 2 colors
				if (hLB < 0) {
					Mat mask1;
					inRange(hsv, Scalar(180+hLB, sLB, vLB), Scalar(180, sUB, vUB), mask1);
					mask += mask1;
				}
				else if (hUB > 180) {
					Mat mask1;
					inRange(hsv, Scalar(0, sLB, vLB), Scalar(hUB-180, sUB, vUB), mask1);
					mask += mask1;
				}

				ori.copyTo(CMask, mask);
				white.copyTo(showALL, mask);

				//imshow("mask", mask);
				//imshow("coloredMask", CMask);
				imshow("HSV", showALL);

				setMouseCallback("mask", OnMouse);
				setMouseCallback("coloredMask", OnMouse);
				setMouseCallback("showAll", OnMouse);

				//reset the target
				X = -1;
				Y = -1;
			}
		}
	}
    return 0;
}

