// SL_graphic_white.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<opencv2/opencv.hpp>
#include<iostream>
#include<vector>
#include<iomanip>
using namespace std;
using namespace cv;

int main()
{
	vector<Point> point;
	int x, y;
	cin >> y;
	while (y != -1) {
		cin >> x;
		point.push_back(Point(x, 255-y));
		cin >> y;
	}
	for (vector<Point>::iterator it = point.begin()+1;it != point.end();it++) {
		vector<Point>::iterator itt = it;
		while (itt != point.begin()) {
			if (itt->y < (itt-1)->y) {
				swap(*itt, *(itt-1));
				itt--;
			}
			else {
				break;
			}
		}
	}
	for (vector<Point>::iterator it = point.begin() + 1;it != point.end();it++) {
		vector<Point>::iterator itt = it;
		while (itt != point.begin()) {
			if (itt->x < (itt - 1)->x) {
				Point temp = *itt;
				*itt = *(itt - 1);
				*(itt - 1) = temp;
				//swap(*itt, *(itt - 1));
				itt--;
			}
			else {
				break;
			}
		}
	}
	Mat show(255, 255, CV_8UC3, Scalar(255, 255, 255));

	line(show, point[0], point[0], Scalar(255, 0, 0), 3);
	for (vector<Point>::iterator it = point.begin() + 1;it != point.end();it++) {
		line(show, *it, *it, Scalar(255, 0, 0), 3);
		//line(show, *it, *(it-1), Scalar(0, 0, 255));
	}
	vector<Point> convex;
	convexHull(point, convex);
	cout << convex[0].x << " " << 255-convex[0].y << endl;
	for (vector<Point>::iterator it = convex.begin() + 1;it != convex.end();it++) {
		line(show, *it, *(it-1), Scalar(0, 0, 255));
		cout << it->x << " " << 255-it->y << endl;
	}
	imshow("show", show);
	waitKey();
    return 0;
}

