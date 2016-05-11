To compile:

javac -cp ./src/ -d . src/org/fitz/ml/main/RunBackProp.java


To Execute:

java org.fitz.ml.main.RunBackProp -experiment <exp> -eta <val> -hidden <val> -iterations <val> -momentum <val>
 	
    * -experiment can be <testIdentity> <testTennis>, <testIris>, <TestIrisNoisy>, <other>
    * if these parameters are left blank, 
      the program will run on the testIdentity dataset


.-----------------------------------.  
| Program files                     |   
'-----------------------------------'  
├── data  
│   └── ann  
│       ├── bool-attr.txt  
│       ├── bool-test.txt  
│       ├── bool-train.txt  
│       ├── identity-attr.txt  
│       ├── identity-train.txt  
│       ├── iris-attr.txt  
│       ├── iris-test.txt  
│       ├── iris-train.txt  
│       ├── tennis-attr.txt  
│       ├── tennis-test.txt  
│       └── tennis-train.txt  
├── run.sh  
└── src  
    └── org  
        └── fitz  
            ├── ml  
            │   ├── Attribute.java                  --> provides functionalities for a attribute  
            │   ├── AttributeType.java              --> specifies attribute types  
            │   ├── Attributes.java                 --> allows us to make a list of attributes  
            │   ├── backprop               
            │   │   ├── Backpropagation.java        --> provides functions for runnning backpropagation  
            │   │   ├── Classifier.java             --> allows us to used stored network to classify examples  
            │   │   ├── Example.java                --> a data instance  
            │   │   ├── FeedForwardNetwork.java     --> provides tools for creating a network  
            │   │   ├── Preprocessor.java           --> preprocesses data  
            │   │   └── Unit.java                   --> a unit in a network layer  
            │   ├── constants  
            │   │   └── AnnConstants.java           --> constants used in the program  
            │   └── main  
            │       └── RunBackProp.java            --> the main class for running th program  
            └── util  
                └── Util.java                       --> provides auxiliary functions  
