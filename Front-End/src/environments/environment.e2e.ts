export const environment = {
  production: false,
  msalConfig: {
    auth: {
      clientId: 'dd063bcd-7ee5-4283-a6b4-76561cc07f64',
      authority: 'https://login.windows-ppe.net/common',
    },
  },
  apiConfig: {
    scopes: ['user.read'],
    uri: 'https://graph.microsoft-ppe.com/v1.0/me',
  },
};
