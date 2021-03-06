// histogramEqualization.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<opencv2\opencv.hpp>
#include<iostream>
#include<sstream>
#include<math.h>

using namespace std;
using namespace cv;

Mat ori;
Mat combine;
Mat white;
int X = -1;
int Y = -1;

void changeImg(const Mat original, Mat &combination, Mat &W) {
	Mat hsv;
	cvtColor(original, hsv, CV_BGR2HSV);

	vector<Mat> channel;
	split(hsv, channel);

	vector<Mat> equalization;
	equalization.push_back(channel[0]);
	//equalization.push_back(Mat());
	equalization.push_back(Mat());
	equalization.push_back(Mat());
	//equalizeHist(channel[0], equalization[0]);
	equalizeHist(channel[1], equalization[1]);
	equalizeHist(channel[2], equalization[2]);

	merge(equalization, combination);

	W = Mat(ori.rows, ori.cols, CV_8UC1, Scalar(255));
}

string int2str(int num) {
	stringstream ss;
	ss << num;
	return ss.str();
}

void OnMouse(int event, int x, int y, int flags, void* param) {
	if (event == CV_EVENT_LBUTTONDOWN) {
		X = x;
		Y = y;
	}
}

int main() {
	int index = 0;

	ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + int2str(index) + ").png");
	white = Mat(ori.rows, ori.cols, CV_8UC1, Scalar(255));

	Mat masked;
	Mat maskedWhite;

	changeImg(ori, combine, white);
	
	while (char c = waitKey(1)) {
		imshow("ori", ori);
		setMouseCallback("ori", OnMouse);

		if (c == 'a'&&index>0) {
			destroyAllWindows();
			index--;
			ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + int2str(index) + ").png");
			white = Mat(ori.rows, ori.cols, CV_8UC1, Scalar(255));
			changeImg(ori, combine, white);
			masked = Mat();
			maskedWhite = Mat();
		}
		else if (c == 's'){
			destroyAllWindows();
			index++;
			ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + int2str(index) + ").png");
			white = Mat(ori.rows, ori.cols, CV_8UC1, Scalar(255));
			changeImg(ori, combine, white);
			masked = Mat();
			maskedWhite = Mat();
		}
		//imshow("com", combine);

		if (X >= 0 && Y >= 0) {
			Vec3b* ptr = combine.ptr<Vec3b>(Y, X);
			int h = ptr->val[0];
			int s = ptr->val[1];
			int v = ptr->val[2];
			
			cout << "H: " << h << endl;
			cout << "S: " << s << endl;
			cout << "V: " << v << endl;// << endl;

			int hRange = 3;
			int sRange = 30;
			int vRange = 160;

			Mat mask;

			if (v <= 35) {
				int upperbound = max(35, v + 5);
				inRange(combine, Scalar(0, 0, 0), Scalar(255, 255, upperbound), mask);
				cout << "v <= 30" << endl << endl;
			}
			else if (s <= 45) {
				vRange = 135;
				inRange(combine, Scalar(0, 0, v-vRange), Scalar(255, 45, v+vRange), mask);
				cout << "s <= 40" << endl << endl;
			}
			else {
				cout << "else" << endl << endl;
				//add
				if (s > 123 && v > 123) {
					hRange = 10;
				}
				//end add
				if (h + hRange > 180) {
					Mat mask1;
					Mat mask2;
					inRange(combine, Scalar(h - hRange, s - sRange, v - vRange), Scalar(181, s + sRange+1, v + vRange + 1), mask1);
					inRange(combine, Scalar(0, s - sRange, v - vRange), Scalar(h + hRange - 180 + 1, s + sRange + 1, v + vRange + 1), mask2);
					mask = mask1 + mask2;
				}
				else if (h - hRange < 0) {
					Mat mask1;
					Mat mask2;
					inRange(combine, Scalar(180 + h - hRange, s - sRange, v - vRange), Scalar(180 + 1, s + sRange + 1, v + vRange + 1), mask1);
					inRange(combine, Scalar(0, s - sRange, v - vRange), Scalar(h + hRange + 1, s + sRange + 1, v + vRange + 1), mask2);
					mask = mask1 + mask2;
				}
				else {
					inRange(combine, Scalar(h - hRange, s - sRange, v - vRange), Scalar(h + hRange + 1, s + sRange, v + vRange), mask);
				}
			}
			ori.copyTo(masked, mask);
			white.copyTo(maskedWhite, mask);

			imshow("mask", mask);
			imshow("masked", masked);
			imshow("maskedWhite", maskedWhite);

			setMouseCallback("mask", OnMouse);
			setMouseCallback("masked", OnMouse);
			setMouseCallback("maskedWhite", OnMouse);

			X = -1;
			Y = -1;
		}
	}

	return 0;
}

