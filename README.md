# Build

In order to build the executable for the kNN evaluation framework, run the following command:

```
./gradlew installDist          //for Linux

or

gradlew installDist            //for Windows
```

Afterwards, the following artifact will be produced:

```
build/install/knn-evaluator/
```

Everything one needs to run evaluations is found under the above mentioned folder.

# Run

In order to run an evaluation, the following executable has to be called:

```
./build/install/knn-evaluator/bin/knn-evaluator
```

## Command line arguments

**SYNOPSIS**

```
knn-extractor -f <data set files> [-c <knn config file>]
```

**DESCRIPTION**

* ***-f*** for specifying the paths of the input data sets that will be taken for evaluation.
The paths need to be separated using "," (comma). In case the given path is a folder, the
program will recursively load all files under that folder. Please consider that the folder
should only contain valid data set files, as supported by WEKA, otherwise the program will
terminate unsuccessfully.

* ***-c*** for specifying the path to the kNN configuration file. This should be a simple java
properties file, containing the following keys (an example is found in the
*src/main/resources/default-config.properties* file, which is also the default if not
specifying this argument):

    * `knn.k` representing the value of *k* used by the kNN classifier.

    * `strategy.<strategyId>` representing the options for one strategy used by the kNN
    classifier. The options should match the format of the *-A* argument of the kNN classifier,
    as defined in WEKA. Any number of strategies can be used in one run and each should have
    a different *strategyId*, usually one that defines that strategy, for example
    *strategy.kdtree*

**EXAMPLES**

Calling the program with the default kNN configuration options and all data sets in a folder:

```
./build/install/knn-evaluator/bin/knn-evaluator -f datasets/
```

Calling the program with the default kNN configuration options and multiple data sets:

```
./build/install/knn-evaluator/bin/knn-evaluator -f "datasets/,/home/ml/dataset1.arff"
```

Calling the program with given kNN configuration file:

```
./build/install/knn-evaluator/bin/knn-evaluator -c knn-configs/kdtree.properties \
    -f "datasets/set1.arff,datasets/set2.arff"
```