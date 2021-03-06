// GrayBgrHsv.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<iostream>
#include<opencv2\opencv.hpp>
using namespace std;
using namespace cv;

VideoCapture video;
Mat frame;
Mat All;

void OnMouse(int event, int x, int y, int flag, void* param) {
	if (event == CV_EVENT_LBUTTONDOWN) {
		Vec3b bgr = frame.at<Vec3b>(y, x);
		int gray(0.299*bgr.val[2] + 0.587*bgr.val[1] + 0.114*bgr.val[0]);
		Mat mask;
		if (gray <= 35) {
			Mat temp;
			cout << "black" << endl<<gray<< endl<< endl;
			int blackRange = 10;
			cvtColor(frame, temp, CV_BGR2GRAY);
			inRange(temp, Scalar(gray - blackRange), Scalar(gray + blackRange+1), mask);
			All += mask;
			imshow("all", All);
		}
		else {
			int b = bgr.val[0];
			int g = bgr.val[1];
			int r = bgr.val[2];

			double avr = (b + g + r) / 3;

			int maxDiff = max(max(abs(b - g), abs(g - r)), abs(b - r));
			int secondDiff = max(max(abs(b - g), abs(g - r)), abs(b - r)) - min(min(abs(b - g), abs(g - r)), abs(b - r));
			int minDiff = min(min(abs(b - g), abs(g - r)), abs(b - r));

			int maxLimit = (int)(0.65*avr);
			int secondLimit = (int)(0.45*avr);
			int minLimit = (int)(0.2*avr);

			if (maxDiff <= maxLimit&&secondDiff <= secondLimit && minDiff <= minLimit) {
				int bRange = (int)((avr / 255) * 50 + ((255 - avr) / 255) * 10);
				int gRange = (int)((avr / 255) * 50 + ((255 - avr) / 255) * 10);
				int rRange = (int)((avr / 255) * 50 + ((255 - avr) / 255) * 10);
				inRange(frame, Scalar(b - bRange, g - gRange, r - rRange), Scalar(b + bRange+1, g + gRange+1, r + rRange+1), mask);
				All += mask;
				cout << "gray" << endl;
				cout << gray << endl;
				cout<<b<<" "<<g<<" "<<r<< endl<< endl;
				imshow("all", All);
			}
			else {
				Mat temp;
				cvtColor(frame, temp, CV_BGR2HSV);
				Vec3b hsv = temp.at<Vec3b>(y, x);
				int h = hsv.val[0];
				int s = hsv.val[1];
				int v = hsv.val[2];
				int range = 30;
				int hRange = 15;
				int sRange = 80;
				int vRange = 45;
				if (h + hRange > 180) {
					Mat mask1, mask2;
					inRange(temp, Scalar(h - hRange, s - sRange, v - vRange), Scalar(181, s + sRange + 1, v + vRange + 1), mask1);
					inRange(temp, Scalar(0, s - sRange, v - vRange), Scalar(h + hRange - 179, s + sRange + 1, v + vRange + 1), mask2);
					mask = mask1 + mask2;
				}
				else if (h - hRange < 0) {
					Mat mask1, mask2;
					inRange(temp, Scalar(0, s - sRange, v - vRange), Scalar(h + hRange + 1, s + sRange + 1, v + vRange + 1), mask1);
					inRange(temp, Scalar(180 + h - hRange, s - sRange, v - vRange), Scalar(181, s + sRange + 1, v + vRange + 1), mask2);
					mask = mask1 + mask2;
				}
				else {
					inRange(temp, Scalar(h - hRange, s - sRange, v - vRange), Scalar(h + hRange + 1, s + sRange + 1, v + vRange + 1), mask);
				}
				All += mask;
				cout << "other" << endl;
				cout << h << " " << s << " " << v << endl;
				cout << gray << endl << endl;
				imshow("all", All);
			}
		}
		Mat clear;
		GaussianBlur(All, clear, Size(5, 5), 0, 0);
		imshow("clear", clear);
		setMouseCallback("all", OnMouse);
	}
}

int main()
{
	video.open("test.mp4");
	video >> frame;
	All = Mat(Size(frame.cols, frame.rows), CV_8U, Scalar(0));
	while (!frame.empty()) {
		resize(frame, frame, Size(frame.cols / 2, frame.rows / 2));
		resize(All, All, Size(frame.cols, frame.rows));
		
		imshow("select", frame);
		setMouseCallback("select", OnMouse);
		char c = waitKey(1);
		while (c == 's') {
			All = Mat(Size(frame.cols, frame.rows), CV_8U, Scalar(0));
			c = waitKey();
		}

		video >> frame;
	}
    return 0;
}

