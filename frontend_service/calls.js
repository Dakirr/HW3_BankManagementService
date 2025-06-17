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