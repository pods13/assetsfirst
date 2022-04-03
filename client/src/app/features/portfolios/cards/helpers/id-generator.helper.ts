/**
 * Source taken from https://gist.github.com/gordonbrander/2230317,
 * should generate up to 10 thousand unique ids without collision
 */
export function generateId() {
  return '_' + Math.random().toString(36).substr(2, 9);
}
