# Project Naming Conventions and Guidelines

Welcome to our project repository! This README provides comprehensive naming conventions and guidelines to ensure consistency and readability in our codebase.

- [Naming Conventions](#naming-conventions)
    - [Package Names](#package-names)
    - [Class Names](#class-names)
    - [Methods](#methods)
        - [Methods in Utility Classes](#methods-in-utility-classes)
        - [Methods in Other Classes](#methods-in-other-classes)
    - [Variable Names](#variable-names)
    - [Object Names](#object-names)
        - [Java Classes](#java-classes)
        - [Utilities](#utilities)
    - [Feature File Names](#feature-file-names)
- [Guidelines](#guidelines)
    - [Feature File Structure](#feature-file-structure)
    - [Tagging](#tagging)
    - [Code Integration](#code-integration)
    - [Team Collaboration](#team-collaboration)
    - [Static Test Data](#static-test-data)

## Naming Conventions

### Package Names
- Use camelCase without spaces.

**Example:**
- `com.project.runnerTest`

### Class Names
- Class names should represent a tab or link name.
- Follow PascalCase (UpperCamelCase).
- Avoid spaces and special characters.
- Ensure the class name is a valid identifier.
- Choose meaningful names.
- For Step Definitions classes, use "StepDef" as a suffix.

**Examples:**
- UI Class Name: `ProgramDetails`
- Step Definition Class Name: `ProgramDetailsStepDef`

### Methods
#### Methods in Utility Classes
- Use verbs in camelCase for method names.

**Examples:**
- `getAllElementsFromDropdown`
- `readInputValues`
- `checkRadioButton`
- `isProcessComplete`

#### Methods in Other Classes
- Use verbs in camel_Case divided with underscores for method names.

**Examples:**
- `get_All_Elements_From_Dropdown`
- `checkbox_Facility`

### Variable Names
- Use camelCase for variable names.

**Examples:**
- `value`
- `data`
- `expectedValue`

### Object Names
#### Java Classes
- The object name should match the class name but in camelCase.

**Examples:**
- Class: `OpportunityDetails`
- Object: `opportunityDetails`

#### Utilities
- Object names should start with the lowercase first letter of each word in the class name.

**Examples:**
- `SeleniumUtils` becomes `su`
- `DataBaseUtils` becomes `dbu`
- `ApiUtils` becomes `au`
- `DateUtils` becomes `du`

### Feature File Names
- Feature files should follow one of these two rules:
    1. Use PascalCase for page names.
    2. Include a task identifier (ID in Jira) under the sprint package.

**Examples:**
- `CreateMaintainEnrollment`
- `USxxxxxx`
- `CP-xxxx`

## Guidelines

### Feature File Structure
- Feature file structure should include the following sections:
    1. Comments.
    2. Feature tags (module name, project tag, and sprint tag). Optional tags: Regression and Smoke.
    3. Feature description.
    4. Scenario tags. Optional tags: Regression and Smoke.
    5. Scenario or Scenario Outline.
    6. Given(And) - provide the initial context or setup for the test.
    7. When(And) - describe the action or event to be tested.
    8. Then(And) - specify the expected outcomes and assertions.
    9. Include examples for Scenario Outline.

### Tagging
- Add '@Regression' tag to scenarios identified for regression testing.

### Code Integration
- Ensure the code runs successfully on your local machine through POM and Jenkins in Sauce Labs before merging to the release branch.

### Team Collaboration
- Contact the Silver Lining team before modifying code in the Test Utils package.
- Avoid committing `.idea` and test data files to the Github repository.
- Pull the latest code from Github before starting your daily activities.
- Commit your changes daily to prevent conflicts.

### Static Test Data
- Use Java/XML files for storing static test data. QEs are responsible for keeping them updated.

Thank you for following these naming conventions and guidelines. They will help us maintain clean and consistent code and improve our collaboration.
