export const environment = {
  production: false,
  msalConfig: {
    auth: {
      clientId: 'ca8e8e55-b28f-495d-b15d-df4e40406f72',
      authority: 'https://login.microsoftonline.com/dd063bcd-7ee5-4283-a6b4-76561cc07f64/v2.0',
    },
  },
  apiConfig: {
    scopes: ['User.Read'],
    uri: 'https://graph.microsoft.com/v1.0/me',
  },
};

