// HSL_test.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<opencv2\opencv.hpp>
#include<iostream>
#include<math.h>
using namespace cv;
using namespace std;

int X, Y;
bool flag = false;

string IntToString(int num)
{
	stringstream ss;
	ss << num;
	string s = ss.str();
	return s;
}

void OnMouse(int event, int x, int y, int flags, void*param) {
	if (event == CV_EVENT_LBUTTONDOWN) {
		X = x;
		Y = y;
		flag = true;
	}
}

int main()
{
	Mat ori;
	//ori = imread("part.png");
	//if(ori.cols>500)resize(ori, ori, Size(0.5*ori.cols, 0.5*ori.rows));
	//ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (2).png");
	Mat temp;
	//cout << ori.cols << " " << ori.rows << endl;
	Mat show;//(ori.rows, ori.cols, CV_8U, Scalar(0));
	Mat show2;//(ori.cols, ori.rows, CV_8U, Scalar(0));
	int index = 1;
	cout << IntToString(index);
	ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" +IntToString(index) + ").png");
	Mat WW(ori.rows, ori.cols, CV_8UC3, Scalar(255, 255, 255));
	//while (ori.empty());
	
	cvtColor(ori, temp, CV_BGR2HLS);
	
	while (char c = waitKey(1)) {
		imshow("choose color", ori);
		setMouseCallback("choose color", OnMouse);
		
		if (c == 'a'||c=='s') {
			destroyAllWindows();
			show2.release();
			Mat show2;
			if (index>0&&c == 'a') {
				index--;
				ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + IntToString(index) + ")"+".png");
				WW = Mat(ori.rows, ori.cols, CV_8UC3, Scalar(255, 255, 255));
				cvtColor(ori, temp, CV_BGR2HLS);
			}
			else if(c=='s') {
				index++;
				ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + IntToString(index) + ")" + ".png");
				WW = Mat(ori.rows, ori.cols, CV_8UC3, Scalar(255, 255, 255));
				cvtColor(ori, temp, CV_BGR2HLS);
			}
		}
		/*
		if (c == 'a' || c == 's' | c == 'd'||c=='f'||c=='g'||c=='h'||c=='j') {
			destroyAllWindows();
			show2.release();
			Mat show2;
		}
		switch (c) {
		case 'a':
			ori = imread("15.png");
			//resize(ori, ori, Size(0.5*ori.cols, 0.5*ori.rows));
			cvtColor(ori, temp, CV_BGR2HLS);
			break;
		case 's':
			ori = imread("CNBC.jpg");
			resize(ori, ori, Size(0.35*ori.cols, 0.35*ori.rows));
			cvtColor(ori, temp, CV_BGR2HLS);
			break;
		case 'd':
			ori = imread("aa.jpg");
			cvtColor(ori, temp, CV_BGR2HLS);
			break;
		case 'f':
			ori = imread("goya.png");
			cvtColor(ori, temp, CV_BGR2HLS);
			break;
		case 'g':
			ori = imread("part.png");
			cvtColor(ori, temp, CV_BGR2HLS);
			break;
		case 'h':
			ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (40).png");
			cvtColor(ori, temp, CV_BGR2HLS);
			break;
		case 'j':
			ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (2).png");
			cvtColor(ori, temp, CV_BGR2HLS);
			break;
		}*/
		if (flag) {
			//imshow("temp", temp);
			show.release();
			Mat show;
			Mat mask;
			//mask.create(Size(temp.cols, temp.rows), CV_8U);
			Vec3b hls_temp = temp.at<Vec3b>(Point(X, Y));
			int H_temp = hls_temp.val[0];	
			int L_temp = hls_temp.val[1];
			int S_temp = hls_temp.val[2];

			Vec3b bgr_ori = ori.at<Vec3b>(Point(X, Y));
			int B_ori = bgr_ori.val[0];
			int G_ori = bgr_ori.val[1];
			int R_ori = bgr_ori.val[2];
			
			int white = 220;
			int black = 30;
			
			int hRange = 4;
			int lRange = 30;
			int sRange = 80;

			/*if (L_temp > 225) {
				hRange = 5;
			}
			if (L_temp > 150) {
				sRange = 200;
			}
			if (S_temp < 30) {
				hRange = 15;
			}*/

			int hUpper = H_temp + hRange;
			int hLower = H_temp - hRange;

			int lUpper = L_temp + lRange;
			int lLower = L_temp - lRange;

			int sUpper = S_temp + sRange;
			int sLower = S_temp - sRange;

			cout <<H_temp<<" "<< L_temp<<" "<<S_temp<<endl;
			//cout << B_ori << " " << G_ori << " " << R_ori << endl<<endl;
			

			if (L_temp >white){
				Mat mask1;
				Mat mask2;
				inRange(temp, Scalar(0, white, 0), Scalar(180, 255, 255), mask1);
				//inRange(temp, Scalar(0, 145, 0), Scalar(255, 255, 50), mask2);
				mask = mask1;//+mask2;
			}
			else if (L_temp < black) {
				inRange(temp, Scalar(0, 0, 0), Scalar(180, black, 100), mask);
			}
			/*
			else if (S_temp <40) {
				lRange = 20;
				inRange(temp, Scalar(0, black, 0), Scalar(255, L_temp+lRange, 30), mask);
			}*/
			else if ((S_temp < 40 && L_temp < 100)||S_temp<20) {
				Mat mask1;
				Mat mask2;
				//int hRange1 = 30;
				int hRange2 = 70;
				inRange(temp, Scalar(H_temp - hRange2, black, 0), Scalar(H_temp+hRange2,100, 40), mask1);
				//inRange(temp, Scalar(0, black, 0), Scalar(255, 100, 40), mask);
				inRange(temp, Scalar(0, black, 0), Scalar(255, 100, 20), mask2);
				mask = mask1 + mask2;
			}
			else if (H_temp < hRange) {
				Mat mask1;
				Mat mask2;
				inRange(temp, Scalar(180+H_temp - hRange, lLower, sLower), Scalar(180, lUpper, sUpper), mask1);
				inRange(temp, Scalar(0, lLower, sLower), Scalar(hRange + H_temp, lUpper, sUpper), mask2);
				mask = mask1 + mask2;
			}
			else if( H_temp>180-hRange) {
				Mat mask1;
				Mat mask2;
				inRange(temp, Scalar(H_temp-hRange, lLower, sLower), Scalar(180, lUpper, sUpper), mask1);
				inRange(temp, Scalar(0, lLower, sLower), Scalar(180+hRange-H_temp, lUpper, sUpper), mask2);
				mask = mask1 + mask2;
			}
			else {
				inRange(temp, Scalar(hLower, lLower, sLower), Scalar(hUpper, lUpper, sUpper), mask);
				/*
				for (int i = 0;i < temp.rows;i++) {
					Vec3b* it = temp.ptr<Vec3b>(i);
					uchar* itt = mask.ptr<uchar>(i);
					for (int j = 0;j < temp.cols;j++) {
						int hDis = min(abs(H_temp - it[j].val[0]), 360 - abs(H_temp - it[j].val[0]));
						if (!(it[j].val[1] < 20 || it[j].val[1] >200 || it[j].val[2] < 20)&& hDis <6) {
								itt[j] = 255;
						}
						else {
								itt[j] = 0;
						}
					}
					//waitKey();
				}*/
			}
			ori.copyTo(show, mask);
			WW.copyTo(show2, mask);
			//show2 = show2 + mask;
			//imshow("show",show);
			//imshow("mask", mask);
			imshow("HSL", show2);
			//waitKey(10);
			//waitKey();
			flag = false;
			setMouseCallback("mask", OnMouse);
			setMouseCallback("showAll", OnMouse);
		}
	}
    return 0;
}

