To compile:

javac -cp ./src/ -d . src/org/fitz/ml/main/RunDTree.java


To Execute:

java org.fitz.ml.main.RunDTree -experiment <exp> -p -a
 	
    * -experiment can be <testTennis>, <testIris>, <TestIrisNoisy>, <other>
    * -p to print the training data before modifying it such as updating continuous attributes
    * -a to print the training data after modifying it such as after updating continuous attributes
    * if these parameters are left blank, 
      the program will run on the testTennis dataset without printing the training data
      Note that -p and -a are optional flags that do not take arguments


.-----------------------------------.
| Program files                     |
'-----------------------------------'

├── data  
│   └── dtree  
│       ├── bool-attr.txt  
│       ├── bool-test.txt  
│       ├── bool-train.txt  
│       ├── iris-attr.txt  
│       ├── iris-test.txt  
│       ├── iris-train.txt  
│       ├── tennis-attr.txt  
│       ├── tennis-test.txt  
│       └── tennis-train.txt  
└── src  
    └── org  
        └── fitz  
            ├── ml  
            │   ├── constants  
            │   │   └── DtreeConstants.java     --> constants such as file paths used in the program  
            │   ├── dtree
            │   │   ├── Attribute.java          --> Provides functionality for working with a single attribute  
            │   │   ├── AttributeType.java      --> Specifies attribute types such as continuous or string   
            │   │   ├── Attributes.java         --> Povides functionality for multiple attributes  
            │   │   ├── Branch.java             --> Facilitates creating and manipulating a tree branch  
            │   │   ├── Classifier.java         --> accepts rule set and instances, output the predictions and accuracy  
            │   │   ├── Learner.java            --> input training examples/instances, output a tree (or rule set)  
            │   │   ├── Node.java               --> Provides functionality for a tree node  
            │   │   ├── Preprocessor.java       --> Preprocesses dataset files, etc  
            │   │   ├── Rule.java               --> Provides functionality for a single rule  
            │   │   ├── Statement.java          --> Part of an antecedent or expression of a rule  
            │   │   └── Tree.java               --> Provides functionality for working with trees  
            │   └── main  
            │       └── RunDTree.java           --> Main class to run program  
            └── util  
                ├── Compute.java                --> Does computation such as entropy and gain  
                ├── DataSorter.java             --> Implements custom comparator to sort data  
                └── Util.java                   --> Provides auxiliary functions  
