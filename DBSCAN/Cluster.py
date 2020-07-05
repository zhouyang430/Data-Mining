from Point import Point
from typing import List


class Cluster:
    _points = List[Point]

    def __init__(self):
        self._open = 0
        self._points = []


    def addPoint(self, point: Point):
        if point not in self._points:
            self._points.append(point)

    def addPoints(self, points: List[Point]):
        for point in points:
            if point not in self._points:
                self._points.append(point)

    def points(self):
        return self._points
