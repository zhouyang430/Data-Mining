class Point:
    _type: int
    _visited: bool
    _noise: bool
    '''
    把数据集抽象成object
    '''
    def __init__(self, x, y, z, o,label):
        self._x = x
        self._y = y
        self._z = z
        self._o = o
        self._label = label
        self._visited = False
        self._type = -1
        self._noise = False

    def x(self):
        return self._x

    def y(self):
        return self._y

    def z(self):
        return self._z

    def o(self):
        return self._o
    
    def label(self):
        return self._label

    '''
    读写object的操作
    '''
    def getLabel(self):
        return self._label

    def setLabel(self, label: int):
        self._label= label
        
    def getVisited(self):
        return self._visited

    def setVisited(self, visited: bool):
        self._visited = visited

    def getType(self):
        return self._type

    def setType(self, type: int):
        self._type = type


    def setNoise(self, noise: bool):
        self._noise = noise

    def getNoise(self) -> bool:
        return self._noise
