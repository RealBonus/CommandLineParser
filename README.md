# CommandLineParser
Java demo project: Command line parsing, handling commands with attributes and parameters.

Demo project contains small user-login logic and small json 'database' with 'products' entries.

## Syntax
```
command -argument parameter -argument 'complex parameter' nextCommand -argument >> thirdCommand
```

Where 'command' - a command from list bellow, '-argument parameter' - list of arguments with (or without) parameters, passed to command.

All commands support '-help' argument, which will display detailed command description and list of supported arguments.

## Available commands:
- user: User related operations.
- list: Print list of products.
- product: Operations with products.
- print: Prints passed output from previous command.
- exit: Exit program
- >>: Pass previous command output to next command.
- help: Displays help.

## Data
### Accounts
User accounts placed in 'users.json' file in project's root directory. Format: 
```json
[{"user": "admin", "pass": "admin"}, ]
```
Only user 'admin' can work with 'product' command.

### Product
Products stored in 'products.json' file in project's root. Format:
```json
[{"amount":0, "price":0.0, "name":"Name"}, ].
```

## Example
```
help >> print -file ~/help.txt
```
Save help to Users/{home}/help.txt

```
user -login admin -pass admin product -create 'Red bike' -setPrice 100500 -setAmount 1 >> print -file report.txt exit
```
Login as admin, create expensive "Red bike", put report in file, and stop program.

```
list -priceFrom 2000 -priceTo 10000 >> print -display -file ~/products2k-10k.txt list >> print -file ~/allProducts.txt
```
Display products with average price, save them into txt, and then save all products to another txt.

```
help -help
```
Display help for help.