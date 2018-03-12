# Declare Extraction

This projects provides enables the automated extraction of declarative process models from natural language texts. The provided code provides an implementation for the approach described in the following paper:

Van der Aa, H., Di Ciccio, C., Leopold, H., & Reijers, H.A. (2018) Extracting Declarative Process Models from Natural Language Text. Currently under submission at the International Conference on Business Process Management.

### Prerequisites

Java JRE 1.8 is required to be able to run the implementation.

The provided source code is set up as a Maven Project, which means that Maven is required to be installed. Detailed instructions on how to install maven are available at: https://maven.apache.org/install.html

### Installing

All required dependencies are provided as Maven dependencies in pom.xml. 

Can be run as a .JAR file or as an Eclipse project

## Running the project

There are two ways to run the implementation.

1. To apply the approach on a single description, the description can be passed as an argument when running the project. E.g.:
java -jar DeclareExtractor.jar "A claim must be created before it can be approved"

2. To apply and evaluate the approach on a collection of descriptions, these descriptions can be provided as a .csv file. 
- The .csv file should be set up using the same structure as declareextraction/DeclareExtraction/input/datacollection.csv 
- The path to the .csv file should be specified using the variable declareExtraction.main.DeclareExtractor.CONSTRAINT_FILE
- This method is performed when running the project without arguments specified

## Built With
* [Maven](https://maven.apache.org/) - Dependency Management

## Acknowledgments

* Stanford CoreNLP 
