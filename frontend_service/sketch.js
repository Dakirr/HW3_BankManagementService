let API_BASE_URL = 'http://localhost:8081/api'

let jsonInput;
let sendButton;
let statusMessage = '';

class JSON_input {
    constructor (name_text, inputs, processJsonInput, x = 50, y = 50, width = 200, height = 100) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.name_text = name_text;
      this.inputs = inputs;
      this.processJsonInput = processJsonInput;
      this.jsonLabel = null;
      this.InputBoxes = [];
      this.sendButton = null;
      this.statusMessage = null;
    }

    display() {
      this.jsonLabel = createP(this.name_text);
      this.jsonLabel.position(this.x, this.y);
      
      for (let i = 0; i < this.inputs.length; i++) { 
        this.InputBoxes.push([])
        this.InputBoxes[i].push(createP(this.inputs[i][0] + " (" + this.inputs[i][1] + ")"))
        this.InputBoxes[i][0].position(this.x, this.y  + 40 + 50 * i);
        if (this.inputs[i][1] == 'text') {
          this.InputBoxes[i].push(createInput("string", this.inputs[i][1]));
        } else if (this.inputs[i][1] == 'int') {
          this.InputBoxes[i].push(createInput(0, 'number'));
        } else if (this.inputs[i][1] == 'float') {
          this.InputBoxes[i].push(createInput(0.0, 'number'));
        }
        this.InputBoxes[i][1].position(this.x, this.y + 50 * i + 80);
      }
      
      this.sendButton = createButton('Send');
      this.sendButton.position(this.x, this.y + this.InputBoxes.length*50 + 60);
      
      this.statusLabel = createP('Status/Output:');
      this.statusLabel.position(this.x, this.y + this.InputBoxes.length*50 + 100); 
      this.statusWindow = createP();
      this.statusWindow.position(this.x, this.y + this.InputBoxes.length*50 + 120); 

      this.sendButton.mousePressed(async () => {
          this.statusWindow.html('Processing...'); 
          try {
            const result = await this.processJsonInput(this.InputBoxes, this.inputs);
            if (typeof result === 'object') {
             this.statusWindow.html(`<pre>${JSON.stringify(result, null, 2)}</pre>`);
            } else {
              this.statusWindow.html(result);
            }
          } catch (error) {
            this.statusWindow.html(`Error: ${error.message}`);
          }
      });
      
    }

}

function base_json_conversion(InputBoxes, inputs) {
  let json = {}
  for (let i = 0; i < InputBoxes.length; i++) {
    if (inputs[i][1] == 'text') {
      json[inputs[i][0]] = InputBoxes[i][1].value();
    } else if (inputs[i][1] == 'int') {
      json[inputs[i][0]] = parseInt(InputBoxes[i][1].value());
    } else if (inputs[i][1] == 'float') {
      json[inputs[i][0]] = parseFloat(InputBoxes[i][1].value());
    }
  }
  console.log(json);
  return json;
}



let A = new JSON_input('Get BankAccount', [['id', 'int']], getBankAccount)
let B = new JSON_input('Get Order', [['id', 'int']], getOrder, 300, 50)
// let C = new JSON_input('Add BankAccount', [['balance', 'float'], ['currency', 'text']], addBankAccount, 550, 50)
// let D = new JSON_input('Add Order', [['items', 'text'], ['total', 'float'], ['bankAccountId', 'int']], addOrder, 800, 50)
// let E = new JSON_input('Update BankAccount', [['id', 'int'], ['delta', 'float']], updateBankAccount, 1050, 50)

function setup() {
    createCanvas(windowWidth, windowHeight);
    background(255);
    A.display();
    B.display();
    // C.display();
    // D.display();
    // E.display();
}

function draw() {
}
