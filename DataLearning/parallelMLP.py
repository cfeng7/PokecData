#!/usr/bin/env python
from __future__ import division
import sys
import math
import random
from pyspark import SparkContext
import numpy as np
import time

#manipulate data
def parseData(line, slaves):
    instance = line.split(', ')
    subset = random.randint(1,slaves)
    return (subset, [float(x) for x in instance])


def add(x, y):
    res = []
    for i in range(len(x)):
        tmpMatrix = []
        for j in range(len(x[i])):
            tmpWeight = []
            for k in range(len(x[i][j])):
                tmpWeight.append(x[i][j][k] + y[i][j][k])
            tmpMatrix.append(tmpWeight)
        res.append(tmpMatrix)
    return res

# read data
def readData(fileName):
    fr = open(fileName, 'r')
    lines = fr.read().strip().splitlines()
    instances = []
    for line in lines:
        instance = line.split(', ')
        instances.append([float(x) for x in instance])
    fr.close()
    return instances


#forward to predict Y and store phi(nets)
def forward(parameters, instance, nodes):
    features = instance[1:]
    nets = []
    nets.append(features)  #initial input layer
    #initial hidden layer phi(nets) to zero
    for i in range(len(nodes)):
        numOfCurrentLayerNodes = nodes[i]
        nets.append([0 for k in xrange(numOfCurrentLayerNodes)])  #initial hidden layer outputs

    #nets.append([0])  #initial the phi(nets) of output Y
    Y = [0 for x in xrange(nodes[len(nodes)-1])]
    for i in range(len(parameters)):
        numOfNodesOfPre = len(parameters[i])
        numOfNodesOfCurrent = len(parameters[i][0])
        for j in range(numOfNodesOfCurrent):
            netJ = 0
            for k in range(numOfNodesOfPre):
                netJ = netJ + parameters[i][k][j] * nets[i][k]
            if netJ > -700:
                nets[i + 1][j] = 1.0 / (1 + math.exp(-1 * netJ))
            else:
                nets[i + 1][j] = 1.0 / (1 + math.exp(700))
            #if i == len(parameters) - 1:
    for j in xrange(len(Y)):
        if nets[len(nets) - 1][j] < 0.5:
            Y[j] = 0
        else:
            Y[j] = 1
    return [Y, nets]


#predict Y
def predict(parameters, instance, nodes):
    features = instance[1:]
    nets = []
    nets.append(features)  #initial input layer
    #initial hidden layer phi(nets) to zero
    for i in range(len(nodes)):
        numOfCurrentLayerNodes = nodes[i]
        nets.append([0 for k in xrange(numOfCurrentLayerNodes)])  #initial hidden layer outputs

    #nets.append([0])  #initial the phi(nets) of output Y
    Y = [0 for x in xrange(nodes[len(nodes)-1])]
    for i in range(len(parameters)):
        numOfNodesOfPre = len(parameters[i])
        numOfNodesOfCurrent = len(parameters[i][0])
        for j in range(numOfNodesOfCurrent):
            netJ = 0
            for k in range(numOfNodesOfPre):
                netJ = netJ + parameters[i][k][j] * nets[i][k]
            if netJ > -700:
                nets[i + 1][j] = 1.0 / (1 + math.exp(-1 * netJ))
            else:
                nets[i + 1][j] = 1.0 / (1 + math.exp(700))
            #if i == len(parameters) - 1:
    maxNet = 0
    for j in xrange(len(Y)):
        if nets[len(nets) - 1][j] > nets[len(nets)-1][maxNet]:
            maxNet = j
    Y[maxNet] = 1
    return Y


#get accuracy
def evaluate(parameters, instances, nodes):
    correct = 0
    numOfYs = nodes[len(nodes)-1]
    for instance in instances:
        label = [0 for x in xrange(numOfYs)]
        label[int(instance[0])-1] = 1
        Y = predict(parameters, instance, nodes)
        c = True;
        for i in range(len(Y)-1):
            if label[i] != Y[i]:
                c = False
        if c == True:
            correct = correct + 1
    #return (1.0 * correct / len(instances))
    return correct


#main function
def train(instances, cmdParameters, parameters):
    #cmdParameters = sys.argv
    if (cmdParameters[1] == 'train'):
        alpha = 1
        nodes = [int(x) for x in cmdParameters[4:]]

        for instance in instances:
            numOfYs = nodes[len(nodes)-1]
            label = [0 for x in xrange(numOfYs)]
            label[int(instance[0])-1] = 1
            preSegma = []

            #forward to get predicted Y
            [Y, nets] = forward(parameters, instance, nodes)

            #backward propagation
            for i in reversed(range(len(nets))):
                if i > 0:
                    numOfNodesOfCurrent = len(nets[i])
                    numOfNodesOfLower = len(nets[i - 1])
                    currentSegma = []
                    if i == len(nets) - 1:
                        for j in range(numOfYs):
                            currentSegma.append((Y[j] - label[j]) * nets[i][j] * (1 - nets[i][j]))
                    else:
                        numOfNodesOfUpper = len(nets[i + 1])
                        for j in range(numOfNodesOfCurrent):
                            tmp = 0
                            for k in range(numOfNodesOfUpper):
                                tmp = tmp + preSegma[k] * parameters[i][j][k]
                            segma = tmp * nets[i][j] * (1 - nets[i][j])
                            currentSegma.append(segma)
                    for j in range(numOfNodesOfLower):
                        for k in range(numOfNodesOfCurrent):
                            parameters[i - 1][j][k] = parameters[i - 1][j][k] - alpha * currentSegma[k] * nets[i - 1][j]
                    preSegma = currentSegma
        return parameters

def test(instances, cmdParameters):
    if cmdParameters[1] == 'test':
        parametersFile = cmdParameters[2]
        fr = open(parametersFile, 'r')
        lines = fr.read().strip().splitlines()
        parameters = []
        node = []
        words = []
        for line in lines:
            #print line
            #print repr(line)
            if line in ['\n', '', ' ']:
                parameters.append(node)
                node = []
            else:
                words = line.split(' ')
                node.append([float(w) for w in words])
        parameters.append(node)
        #print parameters
        fr.close()
        nodes = []
        for i in xrange(len(parameters)):
            nodes.append(len(parameters[i][0]))
        return evaluate(parameters, instances, nodes)

if __name__ == "__main__":
    cmdParameters = sys.argv
    sc = SparkContext(appName="parallelMLP")
    lines = ''
    slaves = 0
    if cmdParameters[1] == 'train':
        lines = sc.textFile(sys.argv[2])
        slaves = int(sys.argv[3])
        nodes = [int(x) for x in sys.argv[4:]]
    elif cmdParameters[1] == 'test':
        lines =  sc.textFile(sys.argv[3])
        slaves = int(sys.argv[4])
    allInstances = lines.map(lambda line: parseData(line, slaves))
    groupData = map(lambda (x, y): (x, list(y)), allInstances.groupByKey().collect())
    instances = []
    for i in range(len(groupData)):
        instances.append(groupData[i][1])
    print "subset number is " + str(len(instances)) + "\n"
    for i in range(len(instances)):
        print "length subset " + str(i) + " is " + str(len(instances[i])) + "\n"
    #print "length subset 0 is " + str(len(instances[0])) + "\n"
    #print "length subset 1 is " + str(len(instances[1])) + "\n"
    #print "length subset 2 is " + str(len(instances[2])) + "\n"
    #print "length subset 3 is " + str(len(instances[3])) + "\n"
    startTime = time.time()
    parallelInstances = sc.parallelize(instances)
    if (sys.argv[1] == 'train'):
        numOfInputs = len(instances[0][0]) - 1
        #numOfNodesOfOutputLayer = int(cmdParameters[3])
        nodes = [int(x) for x in cmdParameters[4:]]
        parameters = []
        parameters.append(
            [[random.random() for k in xrange(nodes[0])] for j
             in xrange(numOfInputs)])

        for i in range(len(nodes) - 1):
            numOfCurrentLayerNodes = nodes[i]
            numOfNextLayerNodes = nodes[i + 1]
            parameters.append([[random.random() for k in
                                xrange(numOfNextLayerNodes)] for j in xrange(numOfCurrentLayerNodes)])
        parameters = parallelInstances.map(lambda data: train(data, cmdParameters, parameters)).reduce(add)
        print ' execution time is ' + str(time.time()-startTime)
        allData = []
        for i in range(len(instances)):
            allData = allData + instances[i]
        #allData = instances[0] + instances[1] + instances[2] + instances[3] 
        print 'final accuracy = ' + str((1.0 * evaluate(parameters, allData, nodes))/len(allData))
        
        parametersMatrix = '\n\n'.join('\n'.join(' '.join(str(x) for x in e) for e in matrix) for matrix in parameters)
        f = open('parameters1', 'w')
        f.write(parametersMatrix)
        f.close()
        
    if (sys.argv[1] == 'test'):
        correct = parallelInstances.map(lambda data: test(data, cmdParameters)).reduce(lambda a, b: a + b)
        allData = []
        for i in range(len(instances)):
            allData = allData + instances[i]
        #allData = instances[0] + instances[1] + instances[2] + instances[3] 
        print 'final accuracy = ' + str((1.0 * correct)/len(allData)) + ' execution time is ' + str(time.time()-startTime)



