// bgrTest.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<opencv2\opencv.hpp>
#include<iostream>
#include<fstream>
using namespace std;
using namespace cv;

Mat ori;
fstream f;

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
		Mat afterEquHist;
		histogramEqualization(ori, afterEquHist);
		
		Vec3b ptr = afterEquHist.at<Vec3b>(y, x);
		int b = ptr.val[0];
		int g = ptr.val[1];
		int r = ptr.val[2];
		f<< b << endl << g << endl << r << endl;
		f << max(max(abs(b - g), abs(g - r)), abs(b - r)) << endl;
		f << min(min(abs(b - g), abs(g - r)), abs(b - r)) << endl << endl;
	}
}

int main()
{
	f.open("recordElse.txt", ios::out);
	VideoCapture capture;
	capture.open("test.mp4");
	capture >> ori;
	while (!ori.empty()) {
		//resize(ori, ori, Size(ori.cols / 2, ori.rows / 2));
		imshow("ori", ori);
		setMouseCallback("ori", OnMouse);
		char c = waitKey(1);
		if (c == 's') {
			waitKey();
		}
		capture >> ori;
	}
    return 0;
}

