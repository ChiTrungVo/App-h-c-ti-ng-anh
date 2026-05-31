import { Account, Client } from 'node-appwrite';

function page(title, message, status = 200) {
  return {
    status,
    html: `<!doctype html>
<html lang="vi">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>${title}</title>
    <style>
      body {
        margin: 0;
        min-height: 100vh;
        display: grid;
        place-items: center;
        background: #f7fbff;
        color: #06263a;
        font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      }
      main {
        width: min(90vw, 440px);
        padding: 32px;
        border: 2px solid #78d6f0;
        border-radius: 28px;
        background: white;
        text-align: center;
        box-shadow: 0 20px 45px rgb(0 86 120 / 14%);
      }
      h1 { margin: 0 0 12px; font-size: 30px; }
      p { margin: 0 0 24px; font-size: 17px; line-height: 1.5; color: #4c5962; }
      a {
        display: inline-block;
        padding: 14px 22px;
        border-radius: 999px;
        background: #087b92;
        color: white;
        font-weight: 700;
        text-decoration: none;
      }
    </style>
  </head>
  <body>
    <main>
      <h1>${title}</h1>
      <p>${message}</p>
      <a href="minlish://verify-email?refresh=1">Mở MinLish</a>
    </main>
  </body>
</html>`
  };
}

export default async ({ req, res, error }) => {
  const url = new URL(req.url || '/', 'https://minlish.local');
  const userId = req.query?.userId || url.searchParams.get('userId');
  const secret = req.query?.secret || url.searchParams.get('secret');

  if (!userId || !secret) {
    const body = page(
      'Liên kết không hợp lệ',
      'Liên kết xác minh email thiếu thông tin userId hoặc secret.',
      400
    );
    return res.send(body.html, body.status, { 'content-type': 'text/html; charset=utf-8' });
  }

  const endpoint = process.env.APPWRITE_ENDPOINT || process.env.APPWRITE_FUNCTION_API_ENDPOINT;
  const projectId = process.env.APPWRITE_PROJECT_ID || process.env.APPWRITE_FUNCTION_PROJECT_ID;

  try {
    const client = new Client().setEndpoint(endpoint).setProject(projectId);
    const account = new Account(client);

    await account.updateVerification(userId, secret);

    const body = page(
      'Email đã được xác minh',
      'Bạn có thể quay lại MinLish và bấm "Tôi đã xác minh" để tiếp tục học.'
    );
    return res.send(body.html, body.status, { 'content-type': 'text/html; charset=utf-8' });
  } catch (err) {
    error(err?.message || String(err));
    const body = page(
      'Xác minh thất bại',
      'Liên kết đã hết hạn hoặc đã được sử dụng. Hãy quay lại app và gửi lại email xác minh.',
      400
    );
    return res.send(body.html, body.status, { 'content-type': 'text/html; charset=utf-8' });
  }
};
