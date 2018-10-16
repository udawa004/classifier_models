# classifier_models

The following classification approaches are developed and implemented on dataset derived from MNIST dataset.

1. K Nearest Neighbor (KNN) Classifier
2. Ridge Regression (RR) Classifier
3. Non Negative Ridge Regression Classifier

Accuracy of classification is used to measure the performance of each classifier.

1) For knn the program uses validation set to mesaure different values of k from 1 to 20 and then selects the value of k that gives the best performance. This selected value of k is used to classify the test data and measure its performance.

2) For Ridge Regression 10 one vs rest binary classifiers are developed. The instance is assigned to the classifier that gives the highest prediction value out of the 10 classifers. The program is tested on different values of the constant for ridge regsstion on the validation data set. The value of constant giving the highest performance is chosen to classify the test data set. The optimization process for Ridge Regresstion terminates when the error doesn't change significantly between successive iterations (error is less than 10e-5).

Language Used: Java
