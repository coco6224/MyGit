// bgrTest.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<opencv2\opencv.hpp>
#include<iostream>
#include<fstream>
#include<iomanip>
using namespace std;
using namespace cv;

Mat ori;
fstream f;
Mat mask;

void histogramEqualization(const Mat in, Mat&out) {
	cvtColor(in, out, CV_BGR2HSV);
	vector<Mat> before, after;
	split(out, before);
	after.push_back(before[0]);
	after.push_back(Mat());
	after.push_back(Mat());
	equalizeHist(before[1], after[1]);
	equalizeHist(before[2], after[2]);
	merge(after, out);
	cvtColor(out, out, CV_HSV2BGR);
}

void OnMouse(int event, int x, int y, int flag, void* param) {
	if (event == CV_EVENT_LBUTTONDOWN) {
		//Mat afterEquHist;
		//histogramEqualization(ori, afterEquHist);
		
		//Vec3b ptr = afterEquHist.at<Vec3b>(y, x);
		Vec3b ptr = ori.at<Vec3b>(y, x);
		int b = ptr.val[0];
		int g = ptr.val[1];
		int r = ptr.val[2];

		double avr = (b + g + r) / 3;

		//f << max(max(abs(b - g), abs(g - r)), abs(b - r)) << endl;
		//f << max(max(abs(b - g), abs(g - r)), abs(b - r)) - min(min(abs(b - g), abs(g - r)), abs(b - r)) << endl;
		//f << min(min(abs(b - g), abs(g - r)), abs(b - r)) << endl << endl;

		int maxDiff = max(max(abs(b - g), abs(g - r)), abs(b - r));
		int secondDiff = max(max(abs(b - g), abs(g - r)), abs(b - r)) - min(min(abs(b - g), abs(g - r)), abs(b - r));
		int minDiff = min(min(abs(b - g), abs(g - r)), abs(b - r));

		
		int maxLimit = (int)(0.65*avr);
		int secondLimit = (int)(0.45*avr);
		int minLimit = (int)(0.2*avr);

		/*
		cout <<setw(4)<< maxDiff << " ";
		cout << setw(4) << secondDiff << " ";
		cout << setw(4) << minDiff << endl;
		*/
		/*
		if (r + g + b < 165&&maxDiff<40) {
			cout << "black" << endl;
			cout << r << " ";
			cout << g << " ";
			cout << b << " ";
			cout << r + g + b << " ";
			cout<< maxDiff << endl << endl;
		}
		else */if (maxDiff<=maxLimit&&secondDiff <= secondLimit && minDiff <= minLimit) {
			cout << "white" << endl;
			cout << b << " ";
			cout << g << " ";
			cout << r << " ";
			cout << r + g + b << " ";
			cout << maxDiff << endl;
			cout << "average = " << avr << endl;
			int bRange = (int)((avr / 255) * 50 + ((255 - avr) / 255) * 10);
			int gRange = (int)((avr / 255) * 50 + ((255 - avr) / 255) * 10);
			int rRange = (int)((avr / 255) * 50 + ((255 - avr) / 255) * 10);
			inRange(ori, Scalar(b - bRange, g - gRange, r - rRange), Scalar(b + bRange, g + gRange, r + rRange), mask);
			imshow("mask", mask);
			setMouseCallback("mask", OnMouse);
			//waitKey();
		}
		else {
			cout << r << " ";
			cout << g << " ";
			cout << b << " ";
			cout<<r+g+b<<" ";
			cout << maxDiff << endl << endl;
		}
	}
}

int main()
{
	f.open("recordWhite.txt", ios::out);
	VideoCapture capture;
	capture.open("test.mp4");
	capture >> ori;
	while (!ori.empty()) {
		resize(ori, ori, Size(ori.cols / 1.5, ori.rows / 1.5));
		imshow("ori", ori);
		setMouseCallback("ori", OnMouse);
		char c = waitKey(1);
		if (c == 's') {
			mask = Mat();
			waitKey();
		}
		capture >> ori;
	}
    return 0;
}

