#include <iostream>
#include <fstream>
#include <stack>

using namespace std;

int main() {

    ifstream fileStream;
    fileStream.open("test", fstream::in);

    int PC = 0;
    char sourceCode[32*1024] = {0};

    char data[32*1024] = {0};
    char *dataPointer = data;

    stack<int> loopEntry;

    while (!fileStream.eof()) {
        char c = fileStream.get();

        switch(c) {
            case '>':
            case '<':
            case '+':
            case '-':
            case '.':
            //case ',':
            case '[':
            case ']':
                sourceCode[PC++] = c;
                break;
            default:
                break;
        }
    }

    cout << endl << "code: " << endl << sourceCode << endl << endl;;

    for (int i = 0; i < PC; i++) {
        char symbol = sourceCode[i];
        switch(symbol) {
            case '>': 
                dataPointer++; 
                break;
            case '<': 
                dataPointer--; 
                break;
            case '+': 
                (*dataPointer)++;
                break;
            case '-': 
                (*dataPointer)--; 
                break;
            case '.': 
                cout << *dataPointer; 
                break;
            //case ',': *dataPointer = sourceCode[++i];
            case '[':
                      // if *dataPointer is 0
                      if ((*dataPointer) == 0) {
                          int semaphore = 1;
                          i += 1;
                          while(semaphore != 0) {
                              if(sourceCode[i] == '['){
                                  semaphore++;
                              } else if (sourceCode[i] == ']') {
                                  semaphore--;
                              } 
                              i++;
                          }
                          i--;
                      } else {
                        loopEntry.push(i);
                      }
                      break;
            case ']':
                      if (*dataPointer) {
                          i = loopEntry.top();
                       } else {
                          loopEntry.pop();
                       }
                      break;
            default: break;
        }
    }

    fileStream.close();
}

