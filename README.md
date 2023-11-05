# Tree Based Image Compression Algorithm  

## Files and Directories

- `App.java`: Entry point of the program 
- `TestFrameWork.java`: Test Framework that generates summary statistics for each test 
- `TestFrameworkIndividual.java`: Test Framework that generates statistics for each image
- [`DataAnalysis/Test231031/PerformanceDataAnalysis_Run1_Non_Norm_CRate.ipynb`](DataAnalysis/Test231031/PerformanceDataAnalysis_Run1_Non_Norm_CRate.ipynb): A sample Jupyter note book for data analysis and scoring 
- [`TestResult/TestResult_20231105_111917.log`](TestResult/TestResult_20231105_111917.log): A sample log from the test framework. 

## Compression Effect 

| Before | After | 
| --- | --- | 
| ![image_b](Original/1254659.png) | ![image_a](Decompressed/1254659.png)| 

## Prerequisite

1. Ensure that you have Java 20 or above installed. 
2. Ensure that you have Python 3.9 or above installed. 

## Usage 

### Test Framework 
1. Modify the `enumerateTests()` function call in `main()` function in `TestFrameWork.java` or `TestFrameworkIndividual.java` with appropriate value for the maximum, minimum, and step for `QuadTreeThreeshold` and `allowedExceedingThresholdFactor`. 

2. Compile the respective TestFrameWork 
```sh
javac TestFrameworkIndividual.java
```

3. Run the test framework 
```sh
java TestFrameworkIndividual
```

4. Update the datasource of the note book into the new test data file. Ensure that they are in the same directory. Run the Jupyter Note Book. 

```py
data = pd.read_csv('IndividualCompressionData_since_test_2023MMSS_HHMMSS.csv')
```


## Team Members 
- [Axel]()
- [Ningzhi](https://github.com/MarkMa512)
- [Ping Jie](https://github.com/pinjieng)
- [Timothy](http://github.com/findtimo)
- [Xun Yi](https://github.com/peek00)
