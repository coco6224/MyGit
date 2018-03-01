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
	ori = imread("google.jpg");
	if(ori.cols>500)resize(ori, ori, Size(0.5*ori.cols, 0.5*ori.rows));
	Mat temp;
	cvtColor(ori, temp, CV_BGR2HLS);
	//cout << ori.cols << " " << ori.rows << endl;
	Mat show, show2;//(ori.rows, ori.cols, CV_8U, Scalar(0));
	while (char c = waitKey(1)) {
		imshow("choose color", ori);
		setMouseCallback("choose color", OnMouse);
		if (c == 'a' || c == 's' | c == 'd') {
			destroyAllWindows();
			show2.release();
			Mat show2;
		}
		switch (c) {
		case 'a':
			ori = imread("google.jpg");
			resize(ori, ori, Size(0.5*ori.cols, 0.5*ori.rows));
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
		}
		if (flag) {
			//imshow("temp", temp);
			show.release();
			Mat show;
			Mat mask;
			mask.create(Size(temp.cols, temp.rows), CV_8U);
			Vec3b hls_temp = temp.at<Vec3b>(Point(X, Y));
			int H_temp = hls_temp.val[0];	
			int L_temp = hls_temp.val[1];		//	L>0.95→白	L<0.05→黑
			int S_temp = hls_temp.val[2];
			cout <<H_temp<<" "<< L_temp<<" "<<S_temp<<endl;
			if (L_temp >220||S_temp<22) {
				Mat mask1;
				Mat mask2;
				inRange(temp, Scalar(0, 220, 0), Scalar(255, 255, 255), mask1);
				inRange(temp, Scalar(0, 20, 0), Scalar(255, 255, 22), mask2);
				mask = mask1 + mask2;
			}
			else if (L_temp < 20) {
				inRange(temp, Scalar(0, 0, 0), Scalar(255, 20, 255), mask);
			}
			else if (H_temp < 6 || H_temp>150) {
				Mat mask1;
				Mat mask2;
				inRange(temp, Scalar(0, 20, 20), Scalar(6, 220, 255), mask1);
				inRange(temp, Scalar(150, 20, 20), Scalar(255, 220, 255), mask2);
				mask = mask1 + mask2;
			}
			else {
				inRange(temp, Scalar(H_temp-6, 20, 20), Scalar(H_temp+6, 220, 255), mask);
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
			ori.copyTo(show2, mask);
			imshow("show",show);
			imshow("mask", mask);
			imshow("showAll", show2);
			//waitKey(10);
			//waitKey();
			flag = false;
		}
	}
    return 0;
}

