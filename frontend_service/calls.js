async function getBankAccount(InputBoxes, inputs) {
  let json = base_json_conversion(InputBoxes, inputs);
  const accountId = json['id'];

  if (isNaN(accountId)) {
    console.error("Invalid ID entered:", accountId);
    return "Error: Invalid ID entered.";
  }
  try {
    const url = `${API_BASE_URL}/get/BankAccount/${accountId}`;
    console.log(`Attempting to fetch from: ${url}`);

    const response = await fetch(url);

    if (!response.ok) {
      const errorText = await response.text();
      console.error('API Error:', response.status, response.statusText, errorText);
      return `Error: ${response.status} ${response.statusText} - ${errorText}`;
      ;
    }
    const bankAccountData = await response.json();
    console.log('API Response:', bankAccountData);
    return bankAccountData;

  } catch (error) { 
    console.error('Fetch Error:', error);
    return `Error: ${error.message}`;
  }
}

async function getOrder(InputBoxes, inputs) {
  let json = base_json_conversion(InputBoxes, inputs);
  const accountId = json['id'];

  if (isNaN(accountId)) {
    console.error("Invalid ID entered:", accountId);
    return "Error: Invalid ID entered.";
  }
  try {
    const url = `${API_BASE_URL}/get/order/${accountId}`;
    console.log(`Attempting to fetch from: ${url}`);

    const response = await fetch(url);

    if (!response.ok) {
      const errorText = await response.text();
      console.error('API Error:', response.status, response.statusText, errorText);
      return `Error: ${response.status} ${response.statusText} - ${errorText}`;
      ;
    }
    const bankAccountData = await response.json();
    console.log('API Response:', bankAccountData);
    return bankAccountData;

  } catch (error) { 
    console.error('Fetch Error:', error);
    return `Error: ${error.message}`;
  }
}

async function addBankAccount(InputBoxes, inputs) {
  let json = base_json_conversion(InputBoxes, inputs);
  const balance = json['balance'];
  const id = json['id'];
  try {
    const url = `${API_BASE_URL}/add/BankAccount`;
    console.log(`Attempting to post to: ${url}`);
    console.log(JSON.stringify({id: id, balance: balance }))
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({id: id, balance: balance })
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('API Error:', response.status, response.statusText, errorText);
      return `Error: ${response.status} ${response.statusText} - ${errorText}`;
    }
    const result = await response.json();
    console.log('API Response:', result);
    return result;

  } catch (error) {
    console.error('Fetch Error:', error);
    return `Error: ${error.message}`;
  }
}



async function addOrder(InputBoxes, inputs) {
  let json = base_json_conversion(InputBoxes, inputs);

  try {
    const url = `${API_BASE_URL}/add/order`;
    console.log(`Attempting to post to: ${url}`);
    console.log(JSON.stringify({amount: json['amount'], description: json['description'], userId: json['userId']}))
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({amount: json['amount'], description: json['description'], userId: json['userId']})
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('API Error:', response.status, response.statusText, errorText);
      return `Error: ${response.status} ${response.statusText} - ${errorText}`;
    }
    const result = await response.json();
    console.log('API Response:', result);
    return result;

  } catch (error) {
    console.error('Fetch Error:', error);
    return `Error: ${error.message}`;
  }
}


async function updateBankAccount(InputBoxes, inputs) {
  let json = base_json_conversion(InputBoxes, inputs);
  const delta = json['delta'];
  const id = json['id'];
  try {
    const url = `${API_BASE_URL}/update/BankAccount/${id}`;
    console.log(`Attempting to post to: ${url}`);
    console.log(JSON.stringify({delta: delta}))
    const response = await fetch(url, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({delta: delta})
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('API Error:', response.status, response.statusText, errorText);
      return `Error: ${response.status} ${response.statusText} - ${errorText}`;
    }
    //const result = await response.json();
    console.log('API Response:', "OK");
    return "OK";

  } catch (error) {
    console.error('Fetch Error:', error);
    return `Error: ${error.message}`;
  }
}
